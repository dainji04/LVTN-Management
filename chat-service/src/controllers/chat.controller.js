// controllers/chat.controller.js  —  Chat Service
//
// Các endpoint phục vụ:
//   Tab "Tin nhắn"  → getDirectConversations
//   Tab "Nhóm"      → getGroupConversations
//   Tìm nhóm       → searchGroups          (trong Chat DB)
//   Tìm bạn bè     → KHÔNG nằm ở đây      (thuộc User Service)
//   Chi tiết / gửi / sửa / xóa tin nhắn

const Conversation      = require('../models/conversation.model.js');
const Message           = require('../models/message.model.js');
const userServiceClient = require('../helpers/userServiceClient.js');

class ChatController {

  // ─── Tab "Tin nhắn" ────────────────────────────────────────────────────────
  // GET /chat/conversations/direct
  static async getDirectConversations(req, res) {
    try {
      const userId = req.user.userId;
      console.log('[getDirectConversations] userId:', userId);
      const conversations = await Conversation.getDirectConversations(userId);
      res.json({ success: true, data: conversations });
    } catch (error) {
      console.error('[getDirectConversations]', error);
      res.status(500).json({ error: error.message });
    }
  }

  // ─── Tab "Nhóm" ────────────────────────────────────────────────────────────
  // GET /chat/conversations/groups
  static async getGroupConversations(req, res) {
    try {
      const userId = req.user.userId;
      const conversations = await Conversation.getGroupConversations(userId);
      res.json({ success: true, data: conversations });
    } catch (error) {
      console.error('[getGroupConversations]', error);
      res.status(500).json({ error: error.message });
    }
  }

  // ─── Tìm kiếm nhóm (Chat DB) ───────────────────────────────────────────────
  // GET /chat/conversations/groups/search?q=...
  // Tìm theo tên nhóm trong Chat DB — không cần gọi User Service
  static async searchGroups(req, res) {
    try {
      const userId = req.user.userId;
      const { q } = req.query;

      if (!q || q.trim().length === 0) {
        return res.status(400).json({ error: 'Thiếu từ khóa tìm kiếm' });
      }

      const groups = await Conversation.searchGroups(userId, q.trim());
      res.json({ success: true, data: groups });
    } catch (error) {
      console.error('[searchGroups]', error);
      res.status(500).json({ error: error.message });
    }
  }

  // ─── Tìm kiếm bạn bè ───────────────────────────────────────────────────────
  // GET /chat/friends/search?q=...
  // Chat Service chỉ là proxy — gọi thẳng sang User Service
  // (hoặc bỏ hẳn endpoint này, để client gọi trực tiếp User Service)
  // static async searchFriends(req, res) {
  //   try {
  //     const userId = req.user.userId;
  //     const { q } = req.query;

  //     if (!q || q.trim().length === 0) {
  //       return res.status(400).json({ error: 'Thiếu từ khóa tìm kiếm' });
  //     }

  //     // Gọi User Service — Chat Service không tự query Users
  //     const friends = await userServiceClient.searchFriends(userId, q.trim());
  //     res.json({ success: true, data: friends });
  //   } catch (error) {
  //     console.error('[searchFriends]', error);
  //     res.status(500).json({ error: error.message });
  //   }
  // }

  // ─── Chi tiết conversation ─────────────────────────────────────────────────
  // GET /chat/conversations/:id
  static async getConversation(req, res) {
    try {
      const { id } = req.params;
      const userId = req.user.userId;

      const isMember = await Conversation.isMember(id, userId);
      if (!isMember) {
        return res.status(403).json({ error: 'Không có quyền truy cập' });
      }

      const [conversation, members] = await Promise.all([
        Conversation.getById(id),
        Conversation.getMembers(id),
      ]);

      res.json({ success: true, data: { ...conversation, members } });
    } catch (error) {
      console.error('[getConversation]', error);
      res.status(500).json({ error: error.message });
    }
  }

  // ─── Tạo conversation 1-1 với bạn bè ──────────────────────────────────────
  // POST /chat/conversations/direct
  // Body: { targetUserId, friendName }
  static async createDirectConversation(req, res) {
    try {
      const userId = req.user.userId;
      const { targetUserId, friendName } = req.body;

      if (!targetUserId) {
        return res.status(400).json({ error: 'Thiếu targetUserId' });
      }

      // Xác nhận quan hệ bạn bè qua User Service trước khi tạo
      const ok = await userServiceClient.areFriends(userId, targetUserId);
      if (!ok) {
        return res.status(403).json({ error: 'Chỉ có thể nhắn tin với bạn bè' });
      }

      const conversationId = await Conversation.createDirectConversation(
        userId, targetUserId, friendName
      );
      res.json({ success: true, data: { conversationId } });
    } catch (error) {
      console.error('[createDirectConversation]', error);
      res.status(500).json({ error: error.message });
    }
  }

  // ─── Lấy tin nhắn ──────────────────────────────────────────────────────────
  // GET /chat/conversations/:id/messages?limit=50&offset=0
  static async getMessages(req, res) {
    try {
      const { id } = req.params;
      const userId = req.user.userId;
      const limit  = Math.min(parseInt(req.query.limit)  || 50, 100); // tối đa 100
      const offset = parseInt(req.query.offset) || 0;

      const isMember = await Conversation.isMember(id, userId);
      if (!isMember) {
        return res.status(403).json({ error: 'Không có quyền truy cập' });
      }

      // Lấy messages (đã batch-fetch user info bên trong model)
      const messages = await Message.getByConversationId(id, limit, offset);

      if (messages.length === 0) {
        return res.json({ success: true, data: [] });
      }

      // ── FIX N+1: Load toàn bộ reactions 1 query, không dùng loop ──────────
      const messageIds = messages.map(m => m.MaTinNhan);
      const allReactions = await Message.getReactionsByMessageIds(messageIds);

      // Group reactions theo messageId
      const reactionMap = {};
      for (const r of allReactions) {
        if (!reactionMap[r.MaTinNhan]) reactionMap[r.MaTinNhan] = [];
        reactionMap[r.MaTinNhan].push(r);
      }

      const result = messages.map(msg => ({
        ...msg,
        reactions: reactionMap[msg.MaTinNhan] || [],
      }));

      // Đánh dấu đã đọc (không await — không chặn response)
      Message.markAsRead(id, userId).catch(() => {});

      res.json({ success: true, data: result });
    } catch (error) {
      console.error('[getMessages]', error);
      res.status(500).json({ error: error.message });
    }
  }

  // ─── Sửa tin nhắn ──────────────────────────────────────────────────────────
  // PATCH /chat/messages/:id
  // Body: { content }
  static async updateMessage(req, res) {
    try {
      const { id }    = req.params;
      const { content } = req.body;
      // FIX: Luôn lấy userId từ JWT token, không bao giờ từ req.body
      const userId    = req.user.userId;

      if (!content || content.trim().length === 0) {
        return res.status(400).json({ error: 'Nội dung không được để trống' });
      }

      const message = await Message.getById(id);
      if (!message) {
        return res.status(404).json({ error: 'Không tìm thấy tin nhắn' });
      }
      if (message.MaNguoiGui !== userId) {
        return res.status(403).json({ error: 'Chỉ có thể sửa tin nhắn của chính mình' });
      }

      await Message.update(id, content.trim());
      const updated = await Message.getById(id);
      res.json({ success: true, data: updated });
    } catch (error) {
      console.error('[updateMessage]', error);
      res.status(500).json({ error: error.message });
    }
  }

  // ─── Xóa tin nhắn ──────────────────────────────────────────────────────────
  // DELETE /chat/messages/:id
  static async deleteMessage(req, res) {
    try {
      const { id } = req.params;
      // FIX: Bỏ fallback sang req.body — nếu không có token thì middleware
      //      auth đã chặn trước rồi, không cần || req.body.userId
      const userId = req.user.userId;

      const message = await Message.getById(id);
      if (!message) {
        return res.status(404).json({ error: 'Không tìm thấy tin nhắn' });
      }
      if (message.MaNguoiGui !== userId) {
        return res.status(403).json({ error: 'Chỉ có thể xóa tin nhắn của chính mình' });
      }

      await Message.delete(id);
      res.json({ success: true, message: 'Đã xóa tin nhắn' });
    } catch (error) {
      console.error('[deleteMessage]', error);
      res.status(500).json({ error: error.message });
    }
  }

  // ─── Thêm / cập nhật reaction ──────────────────────────────────────────────
  // POST /chat/messages/:id/reactions
  // Body: { camXuc }
  static async addReaction(req, res) {
    try {
      const { id }     = req.params;
      const { camXuc } = req.body;
      const userId     = req.user.userId;

      const isMsgExist = await Message.getById(id);
      if (!isMsgExist) {
        return res.status(404).json({ error: 'Không tìm thấy tin nhắn' });
      }

      await Message.addReaction(id, userId, camXuc);
      res.json({ success: true });
    } catch (error) {
      console.error('[addReaction]', error);
      res.status(500).json({ error: error.message });
    }
  }

  // ─── Xóa reaction ──────────────────────────────────────────────────────────
  // DELETE /chat/messages/:id/reactions
  static async removeReaction(req, res) {
    try {
      const { id } = req.params;
      const userId = req.user.userId;

      await Message.removeReaction(id, userId);
      res.json({ success: true });
    } catch (error) {
      console.error('[removeReaction]', error);
      res.status(500).json({ error: error.message });
    }
  }
}

module.exports = ChatController;