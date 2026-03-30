const ChatService = require("../services/chat.service.js");
const AuthMiddleware = require("../middleware/auth.middleware.js");

module.exports = (io) => {

  const chatService = new ChatService(io);

  io.use(AuthMiddleware.verifySocketToken);

  io.on("connection", (socket) => {

    console.log(`Socket connected: ${socket.id} user:${socket.userId}`);

    socket.on("user_connect", async () => {

      await chatService.handleUserConnect(socket, socket.userId);

      socket.emit("connected", {
        userId: socket.userId,
        socketId: socket.id,
        onlineUsers: chatService.getOnlineUsers()
      });

    });


    socket.on("send_message", async (data) => {
      await chatService.sendMessage(socket, data);
    });


    socket.on("create_conversation", async (data) => {
      await chatService.createConversation(socket, data);
    });


    socket.on("typing", (data) => {
      chatService.handleTyping(data.conversationId, socket.userId);
    });


    socket.on("stop_typing", (data) => {
      chatService.handleStopTyping(data.conversationId, socket.userId);
    });


    socket.on("join_conversation", ({ conversationId }) => {
      socket.join(`conversation_${conversationId}`);
    });


    socket.on("leave_conversation", ({ conversationId }) => {
      socket.leave(`conversation_${conversationId}`);
    });


    socket.on("disconnect", async () => {

      if (socket.userId) {
        await chatService.handleUserDisconnect(socket.userId);
      }

      console.log(`Socket disconnected ${socket.id}`);
    });

  });
};