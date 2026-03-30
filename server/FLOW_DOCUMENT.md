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
// Bước 1: Đăng nhập
const res = await fetch('/users/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
});
const data = await res.json();
const token = data.data.token;
const userId = data.data.thongTinUser.maNguoiDung;
localStorage.setItem('token', token);

// Bước 2: Subscribe nhận thông báo realtime
const eventSource = new EventSource(
    `https://api-social.dainji.id.vn/thongbao/subscribe/${userId}`
);
eventSource.addEventListener('notification', (e) => {
    const thongBao = JSON.parse(e.data);
    xuLyThongBao(thongBao); // xử lý theo loại thông báo
});
```

---

## 2. 🔔 Luồng Thông báo Realtime (SSE)

Khi server gửi thông báo, FE nhận object có dạng:
```json
{
  "maNguoiHanhDong": 5,
  "maNguoiNhan": 19,
  "loaiHanhDong": "LOI_MOI_KET_BAN",
  "maDoiTuong": null,
  "daDoc": false,
  "ngayTao": "2026-03-25T..."
}
```

**Xử lý theo `loaiHanhDong`:**
```javascript
function xuLyThongBao(thongBao) {
    switch (thongBao.loaiHanhDong) {
        case 'LOI_MOI_KET_BAN':
            // Hiển thị: "User X muốn kết bạn với bạn"
            // Nút Chấp nhận + Nút Từ chối
            break;
        case 'KET_BAN':
            // Hiển thị: "User X đã chấp nhận lời mời kết bạn"
            break;
    }
}
```

---

## 3. 💌 Luồng Kết bạn

### A gửi lời mời kết bạn cho B:
```
1. POST /thongbao/loi-moi-ket-ban?nguoiGuiId={A}&nguoiNhanId={B}
   → Tạo thông báo loại "LOI_MOI_KET_BAN"
   → B nhận thông báo ngay qua SSE (nếu đang online)
```

### B chấp nhận lời mời:
```
2. POST /ban-be/add?id1={A}&id2={B}
   → Tạo quan hệ bạn bè
   → A nhận thông báo "KET_BAN" qua SSE
3. PATCH /thongbao/{maThongBao}/read
   → Đánh dấu thông báo lời mời đã đọc
```

### B từ chối lời mời:
```
2. PATCH /thongbao/{maThongBao}/read
   → Chỉ đánh dấu đã đọc, không tạo quan hệ bạn bè
```

**Code FE khi nhận thông báo LOI_MOI_KET_BAN:**
```javascript
// Hiển thị 2 nút cho user B
function hienThiLoiMoi(thongBao) {
    // Nút Chấp nhận
    btnChapNhan.onclick = async () => {
        await fetch(`/ban-be/add?id1=${thongBao.maNguoiHanhDong}&id2=${thongBao.maNguoiNhan}`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        await fetch(`/thongbao/${thongBao.maThongBao}/read`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${token}` }
        });
    };

    // Nút Từ chối
    btnTuChoi.onclick = async () => {
        await fetch(`/thongbao/${thongBao.maThongBao}/read`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${token}` }
        });
    };
}
```

---

## 4. 📝 Luồng Bài viết

### Tạo bài viết:
```
1. POST /bai-viet              → Tạo bài viết (kèm ảnh nếu có)
2. GET  /bai-viet              → Lấy tất cả bài viết (newsfeed)
3. GET  /bai-viet/user/{id}   → Lấy bài viết của 1 user (trang cá nhân)
```

---

## 5. 💬 Luồng Chat

> Chat đi qua proxy của Spring Boot: `/chat/...`

### Mở cuộc trò chuyện:
```
1. GET /chat/api/conversations?type=direct  → Lấy danh sách chat
2. GET /chat/api/conversations/{id}/messages → Lấy tin nhắn
3. Socket.IO (realtime) để gửi/nhận tin nhắn mới
```

---

## 6. 🚪 Luồng Đăng xuất

```
1. POST /auth/logout           → Vô hiệu hóa token trên server
2. Xóa token khỏi localStorage
3. eventSource.close()        → Đóng kết nối SSE
4. Chuyển về trang login
```

---

## ⚠️ Lưu ý quan trọng cho FE

| Việc cần làm | Thời điểm |
|---|---|
| Gọi `/thongbao/subscribe/{userId}` | Ngay sau khi login thành công |
| Đóng `eventSource` | Khi logout |
| Gửi `Authorization: Bearer {token}` | Với mọi API cần xác thực |
| Refresh token khi hết hạn | Khi nhận lỗi 401 |

**Token hết hạn sau 1 tiếng** → gọi `POST /auth/refresh` để lấy token mới.
