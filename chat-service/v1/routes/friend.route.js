const express = require("express");
const router = express.Router();

const ChatController = require("../controllers/chat.controller");
const AuthMiddleware = require("../middleware/auth.middleware");

router.get("/",
  AuthMiddleware.verifyToken,
  ChatController.getFriends
);

router.get("/search",
  AuthMiddleware.verifyToken,
  ChatController.searchFriends
);

module.exports = router;
