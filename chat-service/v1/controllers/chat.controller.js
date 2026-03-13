const Conversation = require('../models/conversation.model.js');
const Message = require('../models/message.model.js');
const User = require('../models/user.model.js');
const Friend = require('../models/friend.model.js');

class ChatController {
  // Lấy danh sách conversations
  static async getConversations(req, res) {
    try {
      const userId = req.user.userId; // From authenticated token
      const type = req.query.type; // 'all', 'direct', 'group'
      
      let conversations;
      if (type === 'direct') {
        conversations = await Conversation.getDirectConversations(userId);
      } else if (type === 'group') {
        conversations = await Conversation.getGroupConversations(userId);
      } else {
        conversations = await Conversation.getByUserId(userId);
      }

      res.json({ success: true, data: conversations });
    } catch (error) {
      console.error('Error getting conversations:', error);
      res.status(500).json({ error: error.message });
    }
  }

  // Lấy danh sách bạn bè
  static async getFriends(req, res) {
    try {
      const userId = req.user.userId; // From authenticated token
      
      const friends = await Friend.getFriends(userId);
      res.json({ success: true, data: friends });
    } catch (error) {
      console.error('Error getting friends:', error);
      res.status(500).json({ error: error.message });
    }
  }

  // Tìm kiếm bạn bè
  static async searchFriends(req, res) {
    try {
      const { query } = req.query;
      const userId = req.user.userId; // From authenticated token

      if (!query) {
        return res.status(400).json({ error: 'Search query is required' });
      }

      const friends = await Friend.searchFriends(userId, query);
      res.json({ success: true, data: friends });
    } catch (error) {
      console.error('Error searching friends:', error);
      res.status(500).json({ error: error.message });
    }
  }

  // Lấy chi tiết conversation
  static async getConversation(req, res) {
    try {
      const { id } = req.params;
      const userId = req.user.userId; // From authenticated token

      // Kiểm tra quyền truy cập
      const isMember = await Conversation.isMember(id, userId);
      if (!isMember) {
        return res.status(403).json({ error: 'Access denied' });
      }

      const conversation = await Conversation.getById(id);
      const members = await Conversation.getMembers(id);

      res.json({ 
        success: true, 
        data: { ...conversation, members } 
      });
    } catch (error) {
      console.error('Error getting conversation:', error);
      res.status(500).json({ error: error.message });
    }
  }

  // Lấy tin nhắn trong conversation
  static async getMessages(req, res) {
    try {
      const { id } = req.params;
      const userId = req.user.userId; // From authenticated token
      const limit = parseInt(req.query.limit) || 50;
      const offset = parseInt(req.query.offset) || 0;

      // Kiểm tra quyền truy cập
      const isMember = await Conversation.isMember(id, userId);
      if (!isMember) {
        return res.status(403).json({ error: 'Access denied' });
      }

      const messages = await Message.getByConversationId(id, limit, offset);
      
      // Lấy reactions cho mỗi tin nhắn
      const messagesWithReactions = await Promise.all(
        messages.map(async (msg) => {
          const reactions = await Message.getReactions(msg.MaTinNhan);
          return { ...msg, reactions };
        })
      );

      res.json({ 
        success: true, 
        data: messagesWithReactions 
      });
    } catch (error) {
      console.error('Error getting messages:', error);
      res.status(500).json({ error: error.message });
    }
  }

  // Tìm kiếm users
  static async searchUsers(req, res) {
    try {
      const { query } = req.query;
      const userId = req.user.userId; // From authenticated token

      if (!query) {
        return res.status(400).json({ error: 'Search query is required' });
      }

      const users = await User.search(query, userId);
      res.json({ success: true, data: users });
    } catch (error) {
      console.error('Error searching users:', error);
      res.status(500).json({ error: error.message });
    }
  }

  // Lấy thông tin user
  static async getUser(req, res) {
    try {
      const { id } = req.params;
      const user = await User.getById(id);
      
      if (!user) {
        return res.status(404).json({ error: 'User not found' });
      }

      res.json({ success: true, data: user });
    } catch (error) {
      console.error('Error getting user:', error);
      res.status(500).json({ error: error.message });
    }
  }

  // Cập nhật tin nhắn
  static async updateMessage(req, res) {
    try {
      const { id } = req.params;
      const { content, userId } = req.body;

      const message = await Message.getById(id);
      
      if (!message) {
        return res.status(404).json({ error: 'Message not found' });
      }

      if (message.MaNguoiGui !== userId) {
        return res.status(403).json({ error: 'Access denied' });
      }

      await Message.update(id, content);
      const updatedMessage = await Message.getById(id);

      res.json({ success: true, data: updatedMessage });
    } catch (error) {
      console.error('Error updating message:', error);
      res.status(500).json({ error: error.message });
    }
  }

  // Xóa tin nhắn
  static async deleteMessage(req, res) {
    try {
      const { id } = req.params;
      const userId = req.user?.userId || req.body.userId;

      const message = await Message.getById(id);
      
      if (!message) {
        return res.status(404).json({ error: 'Message not found' });
      }

      if (message.MaNguoiGui !== userId) {
        return res.status(403).json({ error: 'Access denied' });
      }

      await Message.delete(id);

      res.json({ success: true, message: 'Message deleted' });
    } catch (error) {
      console.error('Error deleting message:', error);
      res.status(500).json({ error: error.message });
    }
  }
}

module.exports = ChatController;
