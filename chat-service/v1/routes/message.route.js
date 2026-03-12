const express = require("express");
const router = express.Router();

const ChatController = require("../controllers/chat.controller");
const AuthMiddleware = require("../middleware/auth.middleware");

router.put("/:id",
  AuthMiddleware.verifyToken,
  ChatController.updateMessage
);

router.delete("/:id",
  AuthMiddleware.verifyToken,
  ChatController.deleteMessage
);

module.exports = router;