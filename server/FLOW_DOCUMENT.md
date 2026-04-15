# 🔄 Luồng đi các chức năng - Social Media

**Base URL:** `https://api-social.dainji.id.vn`
 
---

## 0. 🔐 Luồng Đăng ký

```
1. POST /users/register     → Tạo tài khoản mới
2. Server kiểm tra email chưa tồn tại
3. Tạo user mới, trả về thông tin user
```

**Request:**
```json
{
  "email": "user@example.com",
  "matKhau": "password123",
  "hoTen": "Nguyen Van A",
  "ngaySinh": "1990-01-01",
  "gioiTinh": "NAM"
}
```

**Response:**
```json
{
  "code": 201,
  "message": "User created successfully",
  "data": {
    "maNguoiDung": 1,
    "email": "user@example.com",
    "hoTen": "Nguyen Van A",
    "anhDaiDien": null,
    "ngaySinh": "1990-01-01",
    "gioiTinh": "NAM"
  }
}
```

---

## 1. 🔐 Luồng Đăng nhập

```
1. POST /users/login          → Lấy token
2. Lưu token vào localStorage
3. GET /thongbao/subscribe/{userId}  → Mở kết nối SSE để nhận thông báo realtime
4. Giữ kết nối SSE trong suốt phiên làm việc
```

**Code FE:**
```javascript
const res = await fetch('/users/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
});
const data = await res.json();
const token = data.data.token;
const refreshToken = data.data.refreshToken; // Lưu refresh token
const userId = data.data.thongTinUser.maNguoiDung;
localStorage.setItem('token', token);
localStorage.setItem('refreshToken', refreshToken);
 
const eventSource = new EventSource(
    `https://api-social.dainji.id.vn/thongbao/subscribe/${userId}`
);
eventSource.addEventListener('notification', (e) => {
    const thongBao = JSON.parse(e.data);
    xuLyThongBao(thongBao);
});
```

**Refresh Token (khi token hết hạn):**
```javascript
const refreshToken = localStorage.getItem('refreshToken');
const res = await fetch('/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ token: refreshToken })
});
const data = await res.json();
localStorage.setItem('token', data.data.token);
```

---

## 2. 🔔 Luồng Thông báo Realtime (SSE)

Khi server gửi thông báo, FE nhận object có dạng:
```json
{
  "id": 123,
  "maNguoiHanhDong": 5,
  "maNguoiNhan": 19,
  "loaiHanhDong": "LOI_MOI_KET_BAN",
  "maDoiTuong": 7,
  "loaiDoiTuong": "LoiMoiKetBan",
  "daDoc": false,
  "ngayTao": "2026-03-25T..."
}
```

> ⚠️ `maDoiTuong` là ID của `LoiMoiKetBan` — dùng cho các bước tiếp theo

**Xử lý theo `loaiHanhDong`:**
```javascript
function xuLyThongBao(thongBao) {
    switch (thongBao.loaiHanhDong) {
        case 'LOI_MOI_KET_BAN':
            hienThiLoiMoi(thongBao);
            break;
        case 'KET_BAN':
            // Hiển thị: "User X đã chấp nhận lời mời kết bạn"
            break;
    }
}
```
 
---

## 3. 💌 Luồng Kết bạn

### Tổng quan:
```
A gửi lời mời → LoiMoiKetBan được tạo → ThongBao push SSE cho B
B nhận thông báo → Chấp nhận hoặc Từ chối
```
 
---

### Bước 1: A gửi lời mời kết bạn cho B
```
POST /loi-moi/gui?nguoiGuiId={A}&nguoiNhanId={B}
Authorization: Bearer {token}
```

Server thực hiện:
```
→ Kiểm tra A và B đã là bạn bè chưa
→ Kiểm tra A đã gửi lời mời cho B chưa
→ Tạo LoiMoiKetBan { trangThai: "DA_GUI" }
→ Tạo ThongBao { loaiHanhDong: "LOI_MOI_KET_BAN", maDoiTuong: maLoiMoi }
→ SSE push ThongBao đến B ngay lập tức
```

Response:
```json
{
  "code": 200,
  "message": "Đã gửi lời mời kết bạn!",
  "data": {
    "id": 7,
    "maNguoiGui": 5,
    "hoTenNguoiGui": "Nguyen An",
    "anhDaiDienNguoiGui": "https://...",
    "maNguoiNhan": 19,
    "trangThai": "DA_GUI"
  }
}
```
 
---

### Bước 2: B nhận thông báo qua SSE
```json
{
  "id": 123,
  "maNguoiHanhDong": 5,
  "maNguoiNhan": 19,
  "loaiHanhDong": "LOI_MOI_KET_BAN",
  "maDoiTuong": 7,
  "loaiDoiTuong": "LoiMoiKetBan",
  "daDoc": false
}
```

FE hiển thị: **"Nguyen An muốn kết bạn với bạn"** + nút Chấp nhận + nút Từ chối
 
---

### Bước 3A: B chấp nhận lời mời
```
// 1. Cập nhật trạng thái lời mời → CHAP_NHAN
PATCH /loi-moi-ket-ban/{maDoiTuong}/trang-thai?trangThai=CHAP_NHAN
Authorization: Bearer {token}
 
// 2. Tạo quan hệ bạn bè
POST /ban-be/add?id1={maNguoiHanhDong}&id2={maNguoiNhan}&loiMoiId={maDoiTuong}
Authorization: Bearer {token}
 
// 3. Đánh dấu thông báo đã đọc
PATCH /thongbao/{id}/read
Authorization: Bearer {token}
```

Server thực hiện khi gọi `/ban-be/add`:
```
→ Kiểm tra trangThai của LoiMoiKetBan = "CHAP_NHAN"
→ Tạo QuanHeBanBe
→ Tạo ThongBao { loaiHanhDong: "KET_BAN" } → SSE push cho A
→ Tạo TheoDoi (A follow B, không gửi thông báo)
```
 
---

### Bước 3B: B từ chối lời mời
```
// 1. Cập nhật trạng thái lời mời → TU_CHOI
PATCH /loi-moi-ket-ban/{maDoiTuong}/trang-thai?trangThai=TU_CHOI
Authorization: Bearer {token}
 
// 2. Đánh dấu thông báo đã đọc
PATCH /thongbao/{id}/read
Authorization: Bearer {token}
```
 
---

### Code FE hoàn chỉnh:
```javascript
function hienThiLoiMoi(thongBao) {
    const maLoiMoi = thongBao.maDoiTuong;     // ID của LoiMoiKetBan
    const maThongBao = thongBao.id;            // ID của ThongBao
    const nguoiGui = thongBao.maNguoiHanhDong;
    const nguoiNhan = thongBao.maNguoiNhan;
 
    // Nút Chấp nhận
    btnChapNhan.onclick = async () => {
        await fetch(`/loi-moi-ket-ban/${maLoiMoi}/trang-thai?trangThai=CHAP_NHAN`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        await fetch(`/ban-be/add?id1=${nguoiGui}&id2=${nguoiNhan}&loiMoiId=${maLoiMoi}`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        await fetch(`/thongbao/${maThongBao}/read`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${token}` }
        });
    };
 
    // Nút Từ chối
    btnTuChoi.onclick = async () => {
        await fetch(`/loi-moi-ket-ban/${maLoiMoi}/trang-thai?trangThai=TU_CHOI`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        await fetch(`/thongbao/${maThongBao}/read`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${token}` }
        });
    };
}
```
 
---

### Ví dụ thực tế (User 5 gửi lời mời cho User 19):

**User 5 gửi lời mời:**
```javascript
await fetch('/loi-moi-ket-ban/gui?nguoiGuiId=5&nguoiNhanId=19', {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` }
});
// → LoiMoiKetBan id=7 được tạo
// → User 19 nhận SSE ngay lập tức:
// { id: 123, maDoiTuong: 7, loaiHanhDong: "LOI_MOI_KET_BAN" }
```

**User 19 chấp nhận:**
```javascript
// maDoiTuong=7, id=123 lấy từ thông báo SSE
await fetch('/loi-moi-ket-ban/7/trang-thai?trangThai=CHAP_NHAN', {
    method: 'PATCH',
    headers: { 'Authorization': `Bearer ${token}` }
});
await fetch('/ban-be/add?id1=5&id2=19&loiMoiId=7', {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token}` }
});
await fetch('/thongbao/123/read', {
    method: 'PATCH',
    headers: { 'Authorization': `Bearer ${token}` }
});
// → QuanHeBanBe được tạo
// → User 5 nhận SSE: { loaiHanhDong: "KET_BAN" }
// → User 19 follow User 5
```

**User 19 từ chối:**
```javascript
await fetch('/loi-moi-ket-ban/7/trang-thai?trangThai=TU_CHOI', {
    method: 'PATCH',
    headers: { 'Authorization': `Bearer ${token}` }
});
await fetch('/thongbao/123/read', {
    method: 'PATCH',
    headers: { 'Authorization': `Bearer ${token}` }
});
// → Không tạo quan hệ bạn bè
```
 
---

## 4. 📝 Luồng Bài viết

```
1. POST /bai-viet              → Tạo bài viết (kèm ảnh nếu có)
2. GET  /bai-viet              → Lấy tất cả bài viết (newsfeed, phân trang)
3. GET  /bai-viet/{id}         → Lấy chi tiết bài viết
4. GET  /bai-viet/user/{id}   → Lấy bài viết của 1 user (trang cá nhân, phân trang)
5. PUT  /bai-viet/{id}         → Cập nhật bài viết
6. DELETE /bai-viet/{id}       → Xóa bài viết
```

**Tạo bài viết:**
```json
POST /bai-viet
{
  "maNguoiDung": 1,
  "noiDung": "Nội dung bài viết",
  "danhSachAnh": ["url1", "url2"] // optional
}
```

**Cập nhật bài viết:**
```json
PUT /bai-viet/{id}
{
  "noiDung": "Nội dung mới",
  "danhSachAnh": ["url_moi"] // Thay thế ảnh cũ
}
```

**Lấy bài viết (phân trang):**
```
GET /bai-viet?page=0&size=10
GET /bai-viet/user/1?page=0&size=5
```

---

## 5. 💬 Luồng Bình luận

```
1. POST /binh-luan             → Tạo bình luận
2. GET  /binh-luan/bai-viet/{id} → Lấy bình luận của bài viết
3. GET  /binh-luan/replies/{id}  → Lấy reply của bình luận
4. PATCH /binh-luan/{id}        → Cập nhật bình luận
5. DELETE /binh-luan/{id}       → Xóa bình luận
```

**Tạo bình luận:**
```json
POST /binh-luan
{
  "maNguoiDung": 1,
  "maBaiDang": 10,
  "noiDung": "Bình luận hay!",
  "maBinhLuanCha": null // null nếu là bình luận gốc, có giá trị nếu là reply
}
```

**Cập nhật bình luận:**
```
PATCH /binh-luan/5?noiDung=Nội dung mới
```

**Xóa bình luận:**
```
DELETE /binh-luan/5?nguoiXoaId=1
```

---

## 6. ❤️ Luồng Lượt thích

```
1. POST /luot-thich/toggle     → Thích/Bỏ thích bài viết hoặc bình luận
2. GET  /luot-thich/check      → Kiểm tra đã thích chưa
3. GET  /luot-thich/danh-sach  → Danh sách người thích
```

**Toggle like:**
```json
POST /luot-thich/toggle
{
  "maNguoiDung": 1,
  "maDoiTuong": 10,
  "loaiDoiTuong": "BaiViet" // hoặc "BinhLuan"
}
```

**Kiểm tra đã thích:**
```
GET /luot-thich/check?maNguoiDung=1&maDoiTuong=10&loaiDoiTuong=BaiViet
```

**Danh sách người thích:**
```
GET /luot-thich/danh-sach?maDoiTuong=10&loaiDoiTuong=BaiViet
```

---

## 7. 👥 Luồng Theo dõi

```
1. POST /theo-doi              → Theo dõi người dùng (gửi thông báo)
2. POST /theo-doi/khong-thong-bao → Theo dõi ngầm (không thông báo)
3. GET  /theo-doi/user/{id}    → Thống kê theo dõi của user
4. DELETE /theo-doi            → Hủy theo dõi
```

**Theo dõi:**
```json
POST /theo-doi
{
  "maNguoiTheoDoi": 1,
  "maNguoiDuocTheoDoi": 2
}
```

**Hủy theo dõi:**
```json
DELETE /theo-doi
{
  "maNguoiTheoDoi": 1,
  "maNguoiDuocTheoDoi": 2
}
```

**Thống kê:**
```
GET /theo-doi/user/1
// Trả về: số người đang theo dõi, số người được theo dõi, danh sách
```

---

## 8. 👤 Luồng Thông tin User

```
1. GET /users                  → Lấy tất cả users
2. GET /users/{id}             → Lấy thông tin user
3. PUT /users/{id}             → Cập nhật profile
4. DELETE /users/{id}          → Xóa user
```

**Cập nhật user:**
```json
PUT /users/1
{
  "hoTen": "Tên mới",
  "ngaySinh": "1990-01-01",
  "gioiTinh": "NU",
  "anhDaiDien": "url_anh_moi"
}
```

---

## 9. 💬 Luồng Chat

> Chat service được proxy qua Spring Boot: `/chat/...` → forward đến Node.js chat service

```
1. GET /chat/api/conversations?type=direct   → Lấy danh sách cuộc trò chuyện
2. GET /chat/api/conversations/{id}/messages → Lấy tin nhắn của cuộc trò chuyện
3. Socket.IO (realtime) để gửi/nhận tin nhắn mới
4. Các API khác của chat service được forward tự động
```

**Ví dụ proxy:**
- `GET /chat/api/users` → gọi đến Node.js: `GET {nodejs.url}/api/users`
- Tất cả headers (Authorization) được forward

**Xem chi tiết API chat:** [Chat Service API](https://github.com/dainji04/LVTN-Management/blob/main/chat-service/API_TESTING.md)

---

## 10. 🚪 Luồng Đăng xuất

```
1. POST /auth/logout       → Vô hiệu hóa token trên server
2. Xóa token khỏi localStorage
3. eventSource.close()    → Đóng kết nối SSE
4. Chuyển về trang login
```

---

## ⚠️ Lưu ý quan trọng cho FE

| Việc cần làm | Thời điểm |
|---|---|
| Gọi `/thongbao/subscribe/{userId}` | Ngay sau khi login thành công |
| Đóng `eventSource` | Khi logout |
| Gửi `Authorization: Bearer {token}` | Với mọi API cần xác thực |
| Refresh token khi cần | Khi token hết hạn (401 response) |
