# 🔌 WebSocket Events Documentation - Chat Service v1.0

> **Server URL**: `http://localhost:3004`  
> **Authentication**: JWT Token required (passed via socket headers)  
> **Transport**: Socket.IO with fallback to HTTP Long Polling

---

## 🔐 Connection & Authentication

### Initial Connection

**Socket.IO Client Setup:**

```javascript
import io from "socket.io-client";

const socket = io("http://localhost:3004", {
  auth: {
    token: "YOUR_ACCESS_TOKEN",
  },
  reconnection: true,
  reconnectionDelay: 1000,
  reconnectionDelayMax: 5000,
  reconnectionAttempts: 5,
});
```

**Python Client Setup (for testing):**

```python
from socketio import Client

sio = Client(
    auth={'token': 'YOUR_ACCESS_TOKEN'},
    reconnection=True,
    reconnection_attempts=5
)
sio.connect('http://localhost:3004')
```

### Connected Event

**Server Sends:**

```javascript
socket.on("connected", (data) => {
  console.log("Connected to chat service");
  // {
  //   userId: 3,
  //   socketId: "abc123xyz",
  //   onlineUsers: [3, 5, 7, 9]
  // }
});
```

---

## 💬 Messaging Events

### Send Message

**Client Emits:**

```javascript
socket.emit("send_message", {
  conversationId: 1,
  content: "Hello! How are you?",
  attachments: [], // optional
});
```

**Full Example:**

```javascript
socket.emit("send_message", {
  conversationId: 1,
  content: "Hello! How are you?",
  attachments: [
    {
      type: "image",
      url: "https://...",
    },
  ],
});
```

**Server Broadcasts to Conversation:**

```javascript
socket.on("message_received", (data) => {
  // {
  //   id: 100,
  //   conversationId: 1,
  //   senderId: 3,
  //   senderName: "You",
  //   content: 'Hello! How are you?',
  //   createdAt: "2024-08-20T10:30:00Z",
  //   reactions: [],
  //   readBy: [3]
  // }
});
```

---

### Update Message

**Client Emits:**

```javascript
socket.emit("update_message", {
  messageId: 100,
  conversationId: 1,
  content: "Updated message content",
});
```

**Server Broadcasts to Conversation:**

```javascript
socket.on("message_updated", (data) => {
  // {
  //   id: 100,
  //   conversationId: 1,
  //   senderId: 3,
  //   content: 'Updated message content',
  //   updatedAt: "2024-08-20T10:35:00Z"
  // }
});
```

---

### Delete Message

**Client Emits:**

```javascript
socket.emit("delete_message", {
  messageId: 100,
  conversationId: 1,
});
```

**Server Broadcasts to Conversation:**

```javascript
socket.on("message_deleted", (data) => {
  // {
  //   id: 100,
  //   conversationId: 1,
  //   deletedAt: "2024-08-20T10:40:00Z"
  // }
});
```

---

## 👥 Conversation Management Events

### Create Conversation

**Client Emits:**

```javascript
socket.emit("create_conversation", {
  type: "direct", // 'direct' or 'group'
  targetUserId: 5, // for direct conversations
  // OR for groups:
  name: "Project Team",
  memberIds: [3, 5, 7, 9],
});
```

**Server Response:**

```javascript
socket.on("conversation_created", (data) => {
  // {
  //   id: 15,
  //   type: "direct",
  //   participantId: 5,
  //   participantName: "John Doe",
  //   createdAt: "2024-08-20T10:45:00Z"
  // }
});
```

---

### Join Conversation

**Client Emits:**

```javascript
socket.emit("join_conversation", {
  conversationId: 1,
});
```

**Server Response:**

```javascript
// Socket joins the room: 'conversation_1'
// Other users in the room receive:
socket.on("user_joined", (data) => {
  // {
  //   userId: 3,
  //   userName: "You",
  //   conversationId: 1,
  //   timestamp: "2024-08-20T10:50:00Z"
  // }
});
```

---

### Leave Conversation

**Client Emits:**

```javascript
socket.emit("leave_conversation", {
  conversationId: 1,
});
```

**Server Response:**

```javascript
// Socket leaves the room: 'conversation_1'
// Other users in the room receive:
socket.on("user_left", (data) => {
  // {
  //   userId: 3,
  //   userName: "You",
  //   conversationId: 1,
  //   timestamp: "2024-08-20T10:55:00Z"
  // }
});
```

---

## ✍️ Typing Indicators

### User is Typing

**Client Emits:**

```javascript
socket.emit("typing", {
  conversationId: 1,
});

// Typically called while user is typing
// Re-emit every 1-2 seconds while typing
```

**Server Broadcasts to Conversation:**

```javascript
socket.on("user_typing", (data) => {
  // {
  //   userId: 5,
  //   userName: "John Doe",
  //   conversationId: 1,
  //   timestamp: "2024-08-20T11:00:00Z"
  // }
});
```

---

### User Stopped Typing

**Client Emits:**

```javascript
socket.emit("stop_typing", {
  conversationId: 1,
});

// Call this when user stops typing or leaves the textarea
```

**Server Broadcasts to Conversation:**

```javascript
socket.on("user_stopped_typing", (data) => {
  // {
  //   userId: 5,
  //   conversationId: 1,
  //   timestamp: "2024-08-20T11:00:05Z"
  // }
});
```

---

## 😊 Reaction Events

### Add Reaction

**Client Emits:**

```javascript
socket.emit("react_message", {
  messageId: 100,
  emoji: "❤️",
  conversationId: 1,
});
```

**Server Broadcasts to Conversation:**

```javascript
socket.on("reaction_added", (data) => {
  // {
  //   messageId: 100,
  //   emoji: '❤️',
  //   userId: 3,
  //   userName: "You",
  //   count: 1,
  //   totalReactions: 1,
  //   timestamp: "2024-08-20T11:05:00Z"
  // }
});
```

---

### Remove Reaction

**Client Emits:**

```javascript
socket.emit("remove_reaction", {
  messageId: 100,
  emoji: "❤️",
  conversationId: 1,
});
```

**Server Broadcasts to Conversation:**

```javascript
socket.on("reaction_removed", (data) => {
  // {
  //   messageId: 100,
  //   emoji: '❤️',
  //   userId: 3,
  //   userName: "You",
  //   totalReactions: 0,
  //   timestamp: "2024-08-20T11:05:30Z"
  // }
});
```

---

## 👁️ Read Status Events

### Mark as Read

**Client Emits:**

```javascript
socket.emit("mark_as_read", {
  conversation_id: 1,
  messageIds: [95, 96, 97, 98, 99, 100],
  // OR mark entire conversation as read:
  // markAll: true
});
```

**Server Broadcasts to Conversation:**

```javascript
socket.on("messages_marked_read", (data) => {
  // {
  //   conversationId: 1,
  //   userId: 3,
  //   userName: "You",
  //   markedMessageIds: [95, 96, 97, 98, 99, 100],
  //   timestamp: "2024-08-20T11:10:00Z"
  // }
});
```

---

## 🟢 Online Status Events

### User Online

**Server Broadcasts to All Connected Users:**

```javascript
socket.on("user_online", (data) => {
  // {
  //   userId: 5,
  //   userName: "John Doe",
  //   timestamp: "2024-08-20T11:15:00Z"
  // }
});
```

---

### User Offline

**Server Broadcasts to All Connected Users:**

```javascript
socket.on("user_offline", (data) => {
  // {
  //   userId: 5,
  //   userName: "John Doe",
  //   lastSeen: "2024-08-20T11:15:30Z"
  // }
});
```

---

### Get Online Users

**Client Emits:**

```javascript
socket.emit("get_online_users", (onlineUsers) => {
  console.log("Online users:", onlineUsers);
  // [3, 5, 7, 9, 11]
});
```

---

## 🔊 Notifications

### New Notification

**Server Sends (when relevant event occurs):**

```javascript
socket.on("notification", (data) => {
  // {
  //   type: 'message_received' | 'user_online' | 'invitation',
  //   title: 'New Message',
  //   message: 'John Doe sent you a message',
  //   data: {
  //     conversationId: 1,
  //     messageId: 100
  //   },
  //   timestamp: "2024-08-20T11:20:00Z"
  // }
});
```

---

## ⚠️ Error Handling

### Connection Error

```javascript
socket.on("connect_error", (error) => {
  console.error("Connection error:", error);
  // {
  //   message: 'Authentication failed',
  //   code: 'UNAUTHORIZED'
  // }
});
```

### Event Error

```javascript
socket.on("error", (error) => {
  console.error("Socket error:", error);
  // {
  //   event: 'send_message',
  //   message: 'Conversation not found',
  //   code: 'NOT_FOUND'
  // }
});
```

---

## 💡 Best Practices

### 1. Connection Setup

```javascript
const socket = io("http://localhost:3004", {
  auth: { token: accessToken },
  reconnection: true,
  reconnectionDelay: 1000,
  reconnectionDelayMax: 5000,
  reconnectionAttempts: 5,
});

socket.on("connect_error", () => {
  // Refresh token and reconnect
});
```

### 2. Room Management

```javascript
// When user opens a conversation
socket.emit("join_conversation", { conversationId });

// When user closes/leaves a conversation
socket.emit("leave_conversation", { conversationId });
```

### 3. Typing Indicator

```javascript
let typingTimeout;

textInput.addEventListener("input", () => {
  clearTimeout(typingTimeout);
  socket.emit("typing", { conversationId });

  typingTimeout = setTimeout(() => {
    socket.emit("stop_typing", { conversationId });
  }, 2000);
});
```

### 4. Message Read Status

```javascript
// After message is visible in viewport
const observerOptions = {
  threshold: 0.5,
};

const observer = new IntersectionObserver((entries) => {
  entries.forEach((entry) => {
    if (entry.isIntersecting) {
      const messageId = entry.target.dataset.messageId;
      socket.emit("mark_as_read", {
        conversationId,
        messageIds: [messageId],
      });
    }
  });
}, observerOptions);
```

### 5. Error Recovery

```javascript
socket.on("error", (error) => {
  console.error("Socket error:", error);
  // Show error notification to user
  showErrorNotification(error.message);
});

socket.on("disconnect", (reason) => {
  if (reason === "io server disconnect") {
    // Server disconnected, need to reconnect
    socket.connect();
  }
});
```

---

## 🧪 Testing Socket Events

### Using Socket.IO Test Client

```javascript
import ioClient from "socket.io-client";

describe("Chat Socket Events", () => {
  let socket;

  beforeEach((done) => {
    socket = ioClient("http://localhost:3004", {
      auth: { token: "test-token" },
    });
    socket.on("connected", done);
  });

  afterEach(() => {
    socket.close();
  });

  test("should send message", (done) => {
    socket.emit("send_message", {
      conversationId: 1,
      content: "Test message",
    });

    socket.on("message_received", (data) => {
      expect(data.content).toBe("Test message");
      done();
    });
  });
});
```

---

## 📊 Room Structure

```
Socket Server
├── conversation_1
│   ├── User 3 (socket_1)
│   └── User 5 (socket_2)
├── conversation_2
│   ├── User 3 (socket_1)
│   ├── User 7 (socket_3)
│   └── User 9 (socket_4)
└── conversation_3
    └── User 5 (socket_2)
```

**Broadcasting Rules:**

- `socket.to('conversation_1').emit(...)` - Send to all in room except sender
- `socket.in('conversation_1').emit(...)` - Send to all in room including sender
- `io.to('conversation_1').emit(...)` - Send to all from server

---

## 📝 Event Checklist

- [x] `connected` - Xác nhận kết nối
- [x] `send_message` - Gửi tin nhắn
- [x] `message_received` - Nhận tin nhắn
- [x] `update_message` - Cập nhật tin nhắn
- [x] `message_updated` - Thông báo cập nhật
- [x] `delete_message` - Xóa tin nhắn
- [x] `message_deleted` - Thông báo xóa
- [x] `create_conversation` - Tạo cuộc trò chuyện
- [x] `conversation_created` - Thông báo tạo
- [x] `join_conversation` - Tham gia cuộc trò chuyện
- [x] `user_joined` - Thông báo người tham gia
- [x] `leave_conversation` - Rời khỏi cuộc trò chuyện
- [x] `user_left` - Thông báo người rời
- [x] `typing` - Đang gõ
- [x] `user_typing` - Thông báo người đang gõ
- [x] `stop_typing` - Dừng gõ
- [x] `user_stopped_typing` - Thông báo dừng gõ
- [x] `react_message` - Thêm phản ứng
- [x] `reaction_added` - Thông báo phản ứng được thêm
- [x] `remove_reaction` - Xóa phản ứng
- [x] `reaction_removed` - Thông báo phản ứng bị xóa
- [x] `mark_as_read` - Đánh dấu đã đọc
- [x] `messages_marked_read` - Thông báo được đánh dấu đã đọc
- [x] `user_online` - Người dùng online
- [x] `user_offline` - Người dùng offline
- [x] `disconnect` - Ngắt kết nối

---

_Last Updated: 2024-08-20_  
_Version: 1.0_
