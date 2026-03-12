# 📋 API QUICK REFERENCE Version 1
 
Quick reference cho tất cả API endpoints

## 🔐 Authentication (Public)
 chat Service không thực hiện chức năng login, xác thực bằng token jwt được gán vào khi login ở auth service
| Endpoint | Method | Body | Description |
|----------|--------|------|-------------|
| `/api/auth/refresh` | POST | `{"refreshToken": "..."}` | Refresh access token |
| `/api/auth/me` | GET | - | Get current user  |
| `/api/auth/verify` | GET | - | Verify token  |
| `/api/auth/logout` | POST | - | Logout |
 
## 💬 Conversations (Protected)
 
| Endpoint | Method | Params | Description |
|----------|--------|--------|-------------|
| `/api/conversations` | GET | `?type=all\|direct\|group` | Get conversations |
| `/api/conversations/:id` | GET | - | Get conversation details |
| `/api/conversations/:id/messages` | GET | `?limit=50&offset=0` | Get messages |
 
## 👥 Friends (Protected)
 
| Endpoint | Method | Params | Description |
|----------|--------|--------|-------------|
| `/api/friends` | GET | - | Get friends list |
| `/api/friends/search` | GET | `?query=name` | Search friends |
 
## 👤 Users (Protected)
 
| Endpoint | Method | Params | Description |
|----------|--------|--------|-------------|
| `/api/users/:id` | GET | - | Get user by ID |
| `/api/users/search` | GET | `?query=name` | Search users |
 
## 📝 Messages (Protected)
 
| Endpoint | Method | Body | Description |
|----------|--------|------|-------------|
| `/api/messages/:id` | PUT | `{"content": "...", "userId": 1}` | Update message |
| `/api/messages/:id` | DELETE | `{"userId": 1}` | Delete message |
 
## 🏥 System
 
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/health` | GET | Health check (public) |
 
---