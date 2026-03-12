const express = require("express");
const router = express.Router();

const ChatController = require("../controllers/chat.controller");
const AuthMiddleware = require("../middleware/auth.middleware");

router.get("/",
  AuthMiddleware.verifyToken,
  ChatController.getConversations
);

router.get("/:id",
  AuthMiddleware.verifyToken,
  ChatController.getConversation
);

router.get("/:id/messages",
  AuthMiddleware.verifyToken,
  ChatController.getMessages
);

module.exports = router;