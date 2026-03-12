const express = require("express");
const cors = require("cors");
const path = require("path");

const authRoutes = require("./routes/auth.route");
const conversationRoutes = require("./routes/conversation.route");
const userRoutes = require("./routes/user.route");
const messageRoutes = require("./routes/message.route");
const friendRoutes = require("./routes/friend.route");

const app = express();

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));


// health check
app.get("/health", (req, res) => {
  res.json({
    status: "OK",
    service: "Chat Service",
    timestamp: new Date()
  });
});


// routes
app.use("/api/auth", authRoutes);
app.use("/api/conversations", conversationRoutes);
app.use("/api/users", userRoutes);
app.use("/api/messages", messageRoutes);
app.use("/api/friends", friendRoutes);

// error middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: "Something went wrong!" });
});

module.exports = app;