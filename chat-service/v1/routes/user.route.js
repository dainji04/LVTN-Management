const express = require("express");
const router = express.Router();

const ChatController = require("../controllers/chat.controller");
const AuthMiddleware = require("../middleware/auth.middleware");

router.get("/search",
  AuthMiddleware.verifyToken,
  ChatController.searchUsers
);

router.get("/:id",
  AuthMiddleware.verifyToken,
  ChatController.getUser
);

module.exports = router;