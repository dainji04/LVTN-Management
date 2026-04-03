const express = require("express");
const cors = require("cors");
const chatRoutes = require("./routes/chat.route");
const { requestContextMiddleware } = require("./middleware/auth.middleware.js");

const app = express();

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(requestContextMiddleware); // Middleware để khởi tạo AsyncLocalStorage cho mỗi request

// health check
app.get("/health", (req, res) => {
  res.json({
    status: "OK",
    service: "Chat Service",
    timestamp: new Date()
  });
});


// routes
app.use("/api/v1/", chatRoutes);

 
// error middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: "Something went wrong!" });
});

module.exports = app;