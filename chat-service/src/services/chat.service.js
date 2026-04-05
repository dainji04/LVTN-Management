const Conversation = require('../models/conversation.model.js');
const Message = require('../models/message.model.js');

const userServiceClient = require('../helpers/userServiceClient.js');

class ChatService {
  constructor(io) {
    this.io = io;
    this.onlineUsers = new Map(); // Map userId -> socketId
    this.typingUsers = new Map(); // Map conversationId -> Set of userIds
  }

  // User kết nối
  async handleUserConnect(socket, userId) {
    this.onlineUsers.set(userId, socket.id);
    
    // // Cập nhật trạng thái online
    // await User.updateLastActivity(userId);
    
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
    
    // // Cập nhật last activity
    // await User.updateLastActivity(userId);
    
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
      const { conversationId, content, type = 'text', replyTo = null } = data;
      const userId = socket.userId;
      console.log(`User ${userId} sending message to conversation ${conversationId}:`, data);
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
      console.log('Message created:', message);
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
      // const { name , memberIds = [], isGroup = true } = data;
      const { name , memberIds = [] } = data;
      const creatorId = socket.userId;
      console.log(`User ${creatorId} creating conversation with members:`, memberIds);
      // Kiểm tra tất cả members phải là bạn bè
      for (const memberId of memberIds) {
        if (memberId !== creatorId) {
          const areFriends = await userServiceClient.areFriends(creatorId, memberId);
          if (!areFriends) {
            throw new Error(`User ${memberId} không phải là bạn bè của bạn`);
          }
        }
      }

      let conversationId;

      // Nếu là cuộc trò chuyện 1-1
      if ( memberIds.length === 1) {
        const otherUserId = memberIds.find(id => id !== creatorId);
        const otherUser = await userServiceClient.getUserById(otherUserId);
        const friendName = `${otherUser.ho || ''} ${otherUser.ten || ''}`.trim();
        console.log(`Creating direct conversation between ${creatorId} and ${otherUserId} (${friendName})`);
        // Tạo hoặc lấy cuộc trò chuyện 1-1
        conversationId = await Conversation.createDirectConversation(
          creatorId, 
          otherUserId,
          friendName
        );

      } else {
        // Tạo nhóm
        conversationId = await Conversation.create({
          ten: name || 'Cuộc trò chuyện nhóm',
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

      const participantIds = [creatorId, ...memberIds];
      const participants_info = await userServiceClient.getUsersByIds(participantIds);
      console.log("Participants info for new conversation:", participants_info);

      // Lấy thông tin conversation
      const conversation = await Conversation.getById(conversationId);
      

      // Join socket rooms
      socket.join(`conversation_${conversationId}`);
      
      

      // Thông báo cho các members
      participantIds.forEach(memberId => {
        console.log("type of memberId", typeof memberId);
        const memberSocket = this.onlineUsers.get(memberId);
        console.log(`Notifying member ${memberId} about new conversation. Socket: ${memberSocket}`);
        if (memberSocket) {
          this.io.to(memberSocket).emit('conversation_created', { conversation, participants_info });
          this.io.sockets.sockets.get(memberSocket)?.join(`conversation_${conversationId}`);
          console.log("conversation created event emitted:", { conversation});
        }
      });

      return { success: true, conversation };
    } catch (error) {
      console.error('Error creating conversation:', error);
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
    this.io.to(`conversation_${conversationId}`).emit('typing', {
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
      const { messageId, reaction } = data;
      const userId = socket.userId;

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

  // Sửa tin nhắn
  async editMessage(socket, data) {
    try {
      const { messageId, content } = data;
      const userId = socket.userId;
      console.log(`User ${userId} editing message ${messageId}:`, data);

      // Lấy tin nhắn gốc để kiểm tra quyền
      const message = await Message.getById(messageId);
      if (!message) {
        throw new Error('Tin nhắn không tồn tại hoặc đã bị xóa');
      }

      // Chỉ người gửi mới được sửa
      if (message.MaNguoiGui !== userId) {
        throw new Error('Bạn không có quyền sửa tin nhắn này');
      }

      // Chỉ cho sửa tin nhắn dạng text
      if (message.LoaiTinNhan !== 'text') {
        throw new Error('Chỉ có thể sửa tin nhắn văn bản');
      }

      // Cập nhật DB
      await Message.update(messageId, content);

      // Lấy lại tin nhắn sau khi sửa
      const updatedMessage = await Message.getById(messageId);

      // Broadcast cho tất cả members trong conversation
      this.io.to(`conversation_${message.MaCuocTroChuyen}`).emit('message_edited', {
        conversationId: message.MaCuocTroChuyen,
        message: updatedMessage,
      });

      return { success: true, message: updatedMessage };
    } catch (error) {
      console.error('Error editing message:', error);
      socket.emit('error', { message: error.message });
      return { success: false, error: error.message };
    }
  }

  // Xóa tin nhắn (soft delete)
  async deleteMessage(socket, data) {
    try {
      const { messageId } = data;
      const userId = socket.userId;

      // Lấy tin nhắn gốc để kiểm tra quyền
      const message = await Message.getById(messageId);
      if (!message) {
        throw new Error('Tin nhắn không tồn tại hoặc đã bị xóa');
      }

      // Chỉ người gửi hoặc admin nhóm mới được xóa
      const isSender = message.MaNguoiGui === userId;
      // const isAdmin  = await Conversation.isAdmin(message.MaCuocTroChuyen, userId);

      if (!isSender) {
        throw new Error('Bạn không có quyền xóa tin nhắn này');
      }

      // Soft delete
      await Message.delete(messageId);

      // Broadcast cho tất cả members
      this.io.to(`conversation_${message.MaCuocTroChuyen}`).emit('message_deleted', {
        conversationId: message.MaCuocTroChuyen,
        messageId,
        deletedBy: userId,
        deletedAt: new Date(),
      });

      return { success: true };
    } catch (error) {
      console.error('Error deleting message:', error);
      socket.emit('error', { message: error.message });
      return { success: false, error: error.message };
    }
  }

  // Lấy online users
  getOnlineUsers() {
    return Array.from(this.onlineUsers.keys());
  }
}

module.exports = ChatService;
