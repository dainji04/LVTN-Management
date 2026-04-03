// routes/chat.routes.js
const express    = require('express');
const router     = express.Router();
const ChatController = require('../controllers/chat.controller.js');
const authMiddleware = require('../middleware/auth.middleware.js');

// Tất cả route đều yêu cầu xác thực JWT
router.use(authMiddleware.verifyToken);

// ── Tab "Tin nhắn" ──────────────────────────────────────────────────────────
// GET /chat/conversations/direct
router.get('/conversations/direct', ChatController.getDirectConversations);

// ── Tab "Nhóm" ──────────────────────────────────────────────────────────────
// GET /chat/conversations/groups
router.get('/conversations/groups', ChatController.getGroupConversations);

// ── Tìm kiếm nhóm (trong Chat DB) ──────────────────────────────────────────
// GET /chat/conversations/groups/search?q=tên nhóm
router.get('/conversations/groups/search', ChatController.searchGroups);

// ── Chi tiết conversation ───────────────────────────────────────────────────
// GET /chat/conversations/:id
router.get('/conversations/:id', ChatController.getConversation);

// ── Mở chat 1-1 với bạn bè ─────────────────────────────────────────────────
// POST /chat/conversations/direct
// Body: { targetUserId, friendName }
router.post('/conversations/direct', ChatController.createDirectConversation);

// ── Tin nhắn ────────────────────────────────────────────────────────────────
// GET /chat/conversations/:id/messages?limit=50&offset=0
router.get('/conversations/:id/messages', ChatController.getMessages);

// PATCH /chat/messages/:id   Body: { content }
router.patch('/messages/:id', ChatController.updateMessage);

// DELETE /chat/messages/:id
router.delete('/messages/:id', ChatController.deleteMessage);

// ── Reactions ───────────────────────────────────────────────────────────────
// POST   /chat/messages/:id/reactions   Body: { camXuc }
router.post('/messages/:id/reactions', ChatController.addReaction);

// DELETE /chat/messages/:id/reactions
router.delete('/messages/:id/reactions', ChatController.removeReaction);

module.exports = router;