# 📋 API TESTING DOCUMENTATION - Chat Service v1.0

> **Lưu ý**: Tất cả các endpoint được bảo vệ bằng JWT Token trừ `/health`
>
> **Base URL**: `http://localhost:3004/api/v1`  
> **Header yêu cầu**: `Authorization: Bearer {accessToken}`

---

## 🏥 Health Check (Public)

### GET `/health`

Kiểm tra trạng thái dịch vụ.

**Request:**

```bash
curl -X GET http://localhost:3004/health
```

**Response:**

```json
{
  "status": "OK",
  "service": "Chat Service",
  "timestamp": "2024-08-20T10:30:45.123Z"
}
```

---

## 💬 Conversations API

### GET `/conversations/direct`

Lấy danh sách tất cả cuộc trò chuyện trực tiếp (1-1).

**Request:**

```bash
curl -X GET http://localhost:3004/api/v1/conversations/direct \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "type": "direct",
      "participantId": 5,
      "participantName": "John Doe",
      "lastMessage": "See you later!",
      "lastMessageTime": "2024-08-20T10:25:00Z",
      "unreadCount": 2
    }
  ]
}
```

---

### GET `/conversations/groups`

Lấy danh sách tất cả nhóm chat.

**Request:**

```bash
curl -X GET http://localhost:3004/api/v1/conversations/groups \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": 10,
      "name": "Project Team",
      "type": "group",
      "memberCount": 5,
      "avatar": "https://...",
      "lastMessage": "Meeting at 3 PM",
      "lastMessageTime": "2024-08-20T10:20:00Z",
      "unreadCount": 3
    }
  ]
}
```

---

### GET `/conversations/groups/search`

Tìm kiếm nhóm chat.

**Query Parameters:**

- `q` (string, required): Từ khóa tìm kiếm

**Request:**

```bash
curl -X GET "http://localhost:3004/api/v1/conversations/groups/search?q=project" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": 10,
      "name": "Project Team",
      "type": "group",
      "memberCount": 5,
      "avatar": "https://..."
    }
  ]
}
```

---

### GET `/conversations/:id`

Lấy chi tiết một cuộc trò chuyện.

**Path Parameters:**

- `id` (number, required): ID của cuộc trò chuyện

**Request:**

```bash
curl -X GET http://localhost:3004/api/v1/conversations/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "type": "direct",
    "participantId": 5,
    "participantName": "John Doe",
    "participantAvatar": "https://...",
    "createdAt": "2024-08-10T15:30:00Z"
  }
}
```

---

### POST `/conversations/direct`

Tạo hoặc mở một cuộc trò chuyện trực tiếp.

**Request Body:**

```json
{
  "targetUserId": 5,
  "friendName": "John Doe"
}
```

**Request:**

```bash
curl -X POST http://localhost:3004/api/v1/conversations/direct \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "targetUserId": 5,
    "friendName": "John Doe"
  }'
```

**Response:**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "type": "direct",
    "participantId": 5,
    "participantName": "John Doe",
    "createdAt": "2024-08-20T10:35:00Z"
  }
}
```

---

## 💌 Messages API

### GET `/conversations/:id/messages`

Lấy danh sách tin nhắn của một cuộc trò chuyện.

**Path Parameters:**

- `id` (number, required): ID của cuộc trò chuyện

**Query Parameters:**

- `limit` (number, optional, default: 50): Số lượng tin nhắn tối đa
- `offset` (number, optional, default: 0): Vị trí bắt đầu

**Request:**

```bash
curl -X GET "http://localhost:3004/api/v1/conversations/1/messages?limit=20&offset=0" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": 100,
      "conversationId": 1,
      "senderId": 3,
      "senderName": "You",
      "content": "Hello! How are you?",
      "createdAt": "2024-08-20T10:30:00Z",
      "reactions": [
        {
          "emoji": "❤️",
          "count": 1,
          "users": [5]
        }
      ],
      "readBy": [3, 5]
    }
  ],
  "pagination": {
    "total": 150,
    "limit": 20,
    "offset": 0,
    "hasMore": true
  }
}
```

---

### PATCH `/messages/:id`

Cập nhật nội dung tin nhắn.

**Path Parameters:**

- `id` (number, required): ID của tin nhắn

**Request Body:**

```json
{
  "content": "Updated message content"
}
```

**Request:**

```bash
curl -X PATCH http://localhost:3004/api/v1/messages/100 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Updated message content"
  }'
```

**Response:**

```json
{
  "success": true,
  "data": {
    "id": 100,
    "conversationId": 1,
    "senderId": 3,
    "content": "Updated message content",
    "updatedAt": "2024-08-20T10:35:00Z"
  }
}
```

---

### DELETE `/messages/:id`

Xóa một tin nhắn.

**Path Parameters:**

- `id` (number, required): ID của tin nhắn

**Request:**

```bash
curl -X DELETE http://localhost:3004/api/v1/messages/100 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**

```json
{
  "success": true,
  "message": "Message deleted successfully"
}
```

---

## 😊 Reactions API

### POST `/messages/:id/reactions`

Thêm phản ứng cảm xúc vào tin nhắn.

**Path Parameters:**

- `id` (number, required): ID của tin nhắn

**Request Body:**

```json
{
  "emoji": "❤️"
}
```

**Request:**

```bash
curl -X POST http://localhost:3004/api/v1/messages/100/reactions \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "emoji": "❤️"
  }'
```

**Response:**

```json
{
  "success": true,
  "data": {
    "messageId": 100,
    "emoji": "❤️",
    "count": 2,
    "users": [3, 5]
  }
}
```

---

### DELETE `/messages/:id/reactions`

Xóa phản ứng cảm xúc khỏi tin nhắn.

**Path Parameters:**

- `id` (number, required): ID của tin nhắn

**Request Body:**

```json
{
  "emoji": "❤️"
}
```

**Request:**

```bash
curl -X DELETE http://localhost:3004/api/v1/messages/100/reactions \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "emoji": "❤️"
  }'
```

**Response:**

```json
{
  "success": true,
  "message": "Reaction removed successfully"
}
```

---

## ⚠️ Error Handling

### Common Error Responses

**401 Unauthorized** - Token không hợp lệ hoặc hết hạn:

```json
{
  "success": false,
  "error": "Unauthorized"
}
```

**404 Not Found** - Không tìm thấy tài nguyên:

```json
{
  "success": false,
  "error": "Conversation not found"
}
```

**400 Bad Request** - Dữ liệu không hợp lệ:

```json
{
  "success": false,
  "error": "Invalid request parameters"
}
```

**500 Internal Server Error**:

```json
{
  "success": false,
  "error": "Something went wrong!"
}
```

---

## 📌 Testing Tips

1. **Chuẩn bị Token**: Lấy JWT access token từ Auth Service
2. **Cấu hình Headers**: Luôn thêm `Authorization: Bearer {token}`
3. **Kiểm tra Content-Type**: Sử dụng `application/json` cho POST/PATCH requests
4. **Pagination**: Sử dụng `limit` và `offset` để phân trang lớn
5. **Error Handling**: Kiểm tra response `success` flag

---

## 🔄 Postman Collection

Bạn có thể import các request trên vào Postman để test dễ hơn.

---
