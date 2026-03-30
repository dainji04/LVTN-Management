# 🔄 Luồng đi các chức năng - Social Media

**Base URL:** `https://api-social.dainji.id.vn`
 
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
const userId = data.data.thongTinUser.maNguoiDung;
localStorage.setItem('token', token);
 
const eventSource = new EventSource(
    `https://api-social.dainji.id.vn/thongbao/subscribe/${userId}`
);
eventSource.addEventListener('notification', (e) => {
    const thongBao = JSON.parse(e.data);
    xuLyThongBao(thongBao);
});
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
2. GET  /bai-viet              → Lấy tất cả bài viết (newsfeed)
3. GET  /bai-viet/user/{id}   → Lấy bài viết của 1 user (trang cá nhân)
```
 
---

## 5. 💬 Luồng Chat

> Chat đi qua Spring Cloud Gateway: `/chat/...`

```
1. GET /chat/api/conversations?type=direct   → Lấy danh sách chat
2. GET /chat/api/conversations/{id}/messages → Lấy tin nhắn
3. Socket.IO (realtime) để gửi/nhận tin nhắn mới
```
 
---

## 6. 🚪 Luồng Đăng xuất

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
