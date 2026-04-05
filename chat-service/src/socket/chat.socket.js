const ChatService = require("../services/chat.service.js");
const AuthMiddleware = require("../middleware/auth.middleware.js");
const requestContext = require('../utils/requestContext.js');

module.exports = (io) => {

  const chatService = new ChatService(io);

  io.use(AuthMiddleware.verifySocketToken);

  function withContext(socket, handler) {
  return (...args) =>
    requestContext.run(
      {
        token: socket.token,
        userId: socket.userId,
        requestId: Date.now()
      },
      () => handler(...args)
    );
}

  io.on("connection", async (socket) => {

    console.log(`Socket connected: ${socket.id} user:${socket.userId}`);
    
    await chatService.handleUserConnect(socket, socket.userId);

    socket.emit("connected", {
      userId: socket.userId,
      socketId: socket.id,
      onlineUsers: chatService.getOnlineUsers()
    });

    socket.on("send_message",withContext(socket, async (data) => {
      await chatService.sendMessage(socket, data);
    }));


    socket.on("create_conversation",withContext(socket, async (data) => {
      await chatService.createConversation(socket, data);
    }));


    socket.on("typing",withContext(socket, (data) => {
      chatService.handleTyping(data.conversationId, socket.userId);
    }));


    socket.on("stop_typing",withContext(socket, (data) => {
      chatService.handleStopTyping(data.conversationId, socket.userId);
    }));


    socket.on("join_conversation", withContext(socket, ({ conversationId }) => {
      console.log(`User ${socket.userId} joining conversation ${conversationId}`);
      return socket.join(`conversation_${conversationId}`);
    }));


    socket.on("leave_conversation", withContext(socket, ({ conversationId }) => {
      console.log(`User ${socket.userId} leaving conversation ${conversationId}`);
      return socket.leave(`conversation_${conversationId}`);
    }));

    socket.on("react_message", withContext(socket, async (data) => {
      await chatService.addReaction(socket, data);
    }));

    // socket.on("mark_as_read", withContext(socket, async (data) => {
    //   await chatService.markAsRead(socket, data);
    // }));

    socket.on("edit_message", withContext(socket, async (data) => {
      await chatService.editMessage(socket, data);
    }));

    socket.on("delete_message", withContext(socket, async (data) => {
      await chatService.deleteMessage(socket, data);
    }));

    socket.on("disconnect", withContext(socket, async () => {

      if (socket.userId) {
        await chatService.handleUserDisconnect(socket.userId);
      }

      console.log(`Socket disconnected ${socket.id}`);
    }));

  });
};