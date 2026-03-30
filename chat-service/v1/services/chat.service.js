const Conversation = require('../models/conversation.model.js');
const Message = require('../models/message.model.js');
const User = require('../models/user.model.js');
const Friend = require('../models/friend.model.js');

class ChatService {
  constructor(io) {
    this.io = io;
    this.onlineUsers = new Map(); // Map userId -> socketId
    this.typingUsers = new Map(); // Map conversationId -> Set of userIds
  }

  // User kết nối
  async handleUserConnect(socket, userId) {
    this.onlineUsers.set(userId, socket.id);
    
    // Cập nhật trạng thái online
    await User.updateLastActivity(userId);
    
    // Join vào các rooms của conversations
    const conversations = await Conversation.getByUserId(userId);
    conversations.forEach(conv => {
      socket.join(`conversation_${conv.MaCuocTroChuyen}`);
    });

    // Thông báo cho friends về trạng thái online
    this.broadcastUserStatus(userId, 'online');
    
    console.log(`User ${userId} connected with socket ${socket.id}`);
  }

  // User ngắt kết nối
  async handleUserDisconnect(userId) {
    this.onlineUsers.delete(userId);
    
    // Cập nhật last activity
    await User.updateLastActivity(userId);
    
    // Thông báo offline
    this.broadcastUserStatus(userId, 'offline');
    
    console.log(`User ${userId} disconnected`);
  }

  // Broadcast trạng thái user
  broadcastUserStatus(userId, status) {
    this.io.emit('user_status', {
      userId,
      status,
      timestamp: new Date()
    });
  }

  // Gửi tin nhắn
  async sendMessage(socket, data) {
    try {
      const { conversationId, userId, content, type = 'text', replyTo = null } = data;

      // Kiểm tra quyền truy cập
      const isMember = await Conversation.isMember(conversationId, userId);
      if (!isMember) {
        throw new Error('Bạn không có quyền gửi tin nhắn trong cuộc trò chuyện này');
      }

      // Tạo tin nhắn
      const messageId = await Message.create({
        maCuocTroChuyen: conversationId,
        maNguoiGui: userId,
        noiDung: content,
        loaiTinNhan: type,
        traLoiBoi: replyTo
      });

      // Cập nhật conversation
      await Conversation.updateMessageCount(conversationId, messageId);

      // Lấy thông tin tin nhắn đầy đủ
      const message = await Message.getById(messageId);

      // Gửi tin nhắn đến tất cả members trong conversation
      this.io.to(`conversation_${conversationId}`).emit('new_message', {
        conversationId,
        message
      });

      // Stop typing indicator
      this.handleStopTyping(conversationId, userId);

      return { success: true, message };
    } catch (error) {
      console.error('Error sending message:', error);
      socket.emit('error', { message: error.message });
      return { success: false, error: error.message };
    }
  }

  // Tạo cuộc trò chuyện mới (chỉ với bạn bè)
  async createConversation(socket, data) {
    try {
      const { name, creatorId, memberIds = [], isGroup = true } = data;

      // Kiểm tra tất cả members phải là bạn bè
      for (const memberId of memberIds) {
        if (memberId !== creatorId) {
          const areFriends = await Friend.areFriends(creatorId, memberId);
          if (!areFriends) {
            throw new Error(`User ${memberId} không phải là bạn bè của bạn`);
          }
        }
      }

      let conversationId;

      // Nếu là cuộc trò chuyện 1-1
      if (!isGroup && memberIds.length === 2) {
        const otherUserId = memberIds.find(id => id !== creatorId);
        const otherUser = await User.getById(otherUserId);
        const friendName = `${otherUser.ho || ''} ${otherUser.Ten || ''}`.trim();
        
        // Tạo hoặc lấy cuộc trò chuyện 1-1
        conversationId = await Conversation.createDirectConversation(
          creatorId, 
          otherUserId,
          friendName
        );
      } else {
        // Tạo nhóm
        conversationId = await Conversation.create({
          ten: name,
          anhDaiDien: null,
          maNguoiTao: creatorId
        });

        // Thêm creator vào nhóm
        await Conversation.addMember(conversationId, creatorId, null, 1); // VaiTro 1 = admin

        // Thêm các members khác
        for (const memberId of memberIds) {
          if (memberId !== creatorId) {
            await Conversation.addMember(conversationId, memberId, creatorId, 0);
          }
        }
      }

      // Lấy thông tin conversation
      const conversation = await Conversation.getById(conversationId);

      // Join socket rooms
      socket.join(`conversation_${conversationId}`);
      
      // Thông báo cho các members
      memberIds.forEach(memberId => {
        const memberSocket = this.onlineUsers.get(memberId);
        if (memberSocket) {
          this.io.to(memberSocket).emit('new_conversation', { conversation });
          this.io.sockets.sockets.get(memberSocket)?.join(`conversation_${conversationId}`);
        }
      });

      return { success: true, conversation };
    } catch (error) {
      console.error('Error creating conversation:', error);
      socket.emit('error', { message: error.message });
      return { success: false, error: error.message };
    }
  }

  // Tạo cuộc trò chuyện 1-1 với bạn bè
  async createDirectChat(socket, data) {
    try {
      const { userId, friendId } = data;

      // Kiểm tra có phải bạn bè không
      const areFriends = await Friend.areFriends(userId, friendId);
      if (!areFriends) {
        throw new Error('Bạn chỉ có thể nhắn tin với bạn bè');
      }

      // Tìm hoặc tạo cuộc trò chuyện
      const friend = await User.getById(friendId);
      const friendName = `${friend.ho || ''} ${friend.Ten || ''}`.trim();
      
      const conversationId = await Conversation.createDirectConversation(
        userId, 
        friendId,
        friendName
      );

      const conversation = await Conversation.getById(conversationId);

      // Join rooms
      socket.join(`conversation_${conversationId}`);
      const friendSocket = this.onlineUsers.get(friendId);
      if (friendSocket) {
        this.io.sockets.sockets.get(friendSocket)?.join(`conversation_${conversationId}`);
      }

      socket.emit('conversation_opened', { conversation });

      return { success: true, conversationId };
    } catch (error) {
      console.error('Error creating direct chat:', error);
      socket.emit('error', { message: error.message });
      return { success: false, error: error.message };
    }
  }

  // Thêm thành viên vào nhóm (chỉ bạn bè)
  async addMemberToGroup(socket, data) {
    try {
      const { conversationId, userId, newMemberId, addedBy } = data;

      // Kiểm tra conversation có phải là nhóm không
      const conversation = await Conversation.getById(conversationId);
      if (conversation.SoLuongThanhVien === 2) {
        throw new Error('Không thể thêm thành viên vào cuộc trò chuyện 1-1');
      }

      // Kiểm tra người thêm có trong nhóm không
      const isMember = await Conversation.isMember(conversationId, addedBy);
      if (!isMember) {
        throw new Error('Bạn không có quyền thêm thành viên');
      }

      // Kiểm tra người được thêm có phải bạn bè của người thêm không
      const areFriends = await Friend.areFriends(addedBy, newMemberId);
      if (!areFriends) {
        throw new Error('Bạn chỉ có thể thêm bạn bè vào nhóm');
      }

      // Thêm thành viên
      await Conversation.addMember(conversationId, newMemberId, addedBy, 0);

      // Thông báo cho tất cả thành viên
      const newMember = await User.getById(newMemberId);
      this.io.to(`conversation_${conversationId}`).emit('member_added', {
        conversationId,
        member: newMember,
        addedBy
      });

      // Cho member mới join room
      const newMemberSocket = this.onlineUsers.get(newMemberId);
      if (newMemberSocket) {
        this.io.sockets.sockets.get(newMemberSocket)?.join(`conversation_${conversationId}`);
        this.io.to(newMemberSocket).emit('added_to_group', { conversation });
      }

      return { success: true };
    } catch (error) {
      console.error('Error adding member:', error);
      socket.emit('error', { message: error.message });
      return { success: false, error: error.message };
    }
  }

  // Typing indicator
  handleTyping(conversationId, userId) {
    if (!this.typingUsers.has(conversationId)) {
      this.typingUsers.set(conversationId, new Set());
    }
    
    this.typingUsers.get(conversationId).add(userId);
    
    // Broadcast typing status
    this.io.to(`conversation_${conversationId}`).emit('user_typing', {
      conversationId,
      userId,
      isTyping: true
    });
  }

  handleStopTyping(conversationId, userId) {
    if (this.typingUsers.has(conversationId)) {
      this.typingUsers.get(conversationId).delete(userId);
      
      // Broadcast stop typing
      this.io.to(`conversation_${conversationId}`).emit('user_typing', {
        conversationId,
        userId,
        isTyping: false
      });
    }
  }

  // Thêm reaction
  async addReaction(socket, data) {
    try {
      const { messageId, userId, reaction } = data;

      await Message.addReaction(messageId, userId, reaction);

      // Lấy tin nhắn để biết conversationId
      const message = await Message.getById(messageId);
      
      // Lấy tất cả reactions
      const reactions = await Message.getReactions(messageId);

      // Broadcast reaction update
      this.io.to(`conversation_${message.MaCuocTroChuyen}`).emit('reaction_update', {
        messageId,
        reactions
      });

      return { success: true };
    } catch (error) {
      console.error('Error adding reaction:', error);
      socket.emit('error', { message: error.message });
      return { success: false, error: error.message };
    }
  }

  // Đánh dấu đã đọc
  async markAsRead(socket, data) {
    try {
      const { conversationId, userId } = data;

      await Message.markAsRead(conversationId, userId);

      // Broadcast read status
      this.io.to(`conversation_${conversationId}`).emit('messages_read', {
        conversationId,
        userId,
        timestamp: new Date()
      });

      return { success: true };
    } catch (error) {
      console.error('Error marking as read:', error);
      return { success: false, error: error.message };
    }
  }

  // Lấy online users
  getOnlineUsers() {
    return Array.from(this.onlineUsers.keys());
  }
}

module.exports = ChatService;
