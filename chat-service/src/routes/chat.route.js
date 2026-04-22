// routes/chat.routes.js
const express    = require('express');
const router     = express.Router();
const ChatController = require('../controllers/chat.controller.js');
const authMiddleware = require('../middleware/auth.middleware.js');

router.use(authMiddleware.verifyToken);

// ── Tab "Tin nhắn" 
router.get('/conversations/direct', ChatController.getDirectConversations);

// ── Tab "Nhóm" 
router.get('/conversations/groups', ChatController.getGroupConversations);

// ── Tìm kiếm nhóm (trong Chat DB)
router.get('/conversations/groups/search', ChatController.searchGroups);

// ── Chi tiết conversation 
router.get('/conversations/:id', ChatController.getConversation);

// ── Mở chat 1-1 với bạn bè
router.post('/conversations/direct', ChatController.createDirectConversation);

// ── Tin nhắn
router.get('/conversations/:id/messages', ChatController.getMessages);

router.patch('/messages/:id', ChatController.updateMessage);

router.delete('/messages/:id', ChatController.deleteMessage);

// ── Reactions
router.post('/messages/:id/reactions', ChatController.addReaction);

router.delete('/messages/:id/reactions', ChatController.removeReaction);

module.exports = router;