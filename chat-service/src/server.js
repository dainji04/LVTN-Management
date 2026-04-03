require('dotenv').config();
const http = require('http');
const { Server } = require('socket.io');

const app = require('./app');
const initSocket = require('./socket/chat.socket');

const server = http.createServer(app);

const io = new Server(server, {
  cors: {
    origin: process.env.CORS_ORIGIN,
    methods: ["GET", "POST"],
    credentials: true
  }
});

// khởi tạo socket events
initSocket(io);

const PORT = process.env.PORT;

server.listen(PORT, () => {
  console.log(`Chat Service running on port ${PORT}`);
});

module.exports = { server, io };