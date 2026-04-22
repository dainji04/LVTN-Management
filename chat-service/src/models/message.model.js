const db = require('../config/cloud.config.js');
const userServiceClient = require('../helpers/userServiceClient.js');

class Message {
  
  static async create({
    maCuocTroChuyen,
    maNguoiGui,
    noiDung,
    loaiTinNhan = 'text',
    duongDanFile = null,
    tenFile = null,
    kichThuocFile = null,
    traLoiBoi = null,
    chuyenTiepTu = null,
  }) {
    const [result] = await db.execute(
      `INSERT INTO TinNhan (
        MaCuocTroChuyen, MaNguoiGui, NoiDung, LoaiTinNhan,
        DuongDanFile, TenFile, KichThuocFile, TraLoiBoi,
        ChuyenTiepTu, DaXoa, DaChinhSua, NgayGuiTinNhan
      ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, NOW())`,
      [
        maCuocTroChuyen, maNguoiGui, noiDung, loaiTinNhan,
        duongDanFile, tenFile, kichThuocFile, traLoiBoi,
        chuyenTiepTu,
      ]
    );
    return result.insertId;
  }

  static async getById(messageId) {
    const [rows] = await db.execute(
      `SELECT * FROM TinNhan WHERE MaTinNhan = ? AND DaXoa = 0`,
      [messageId]
    );
    if (!rows[0]) return null;

    const sender = await userServiceClient.getUserById(rows[0].MaNguoiGui);
    return {
      ...rows[0],
      TenNguoiGui: sender?.ten ?? null,
      HoNguoiGui: sender?.ho ?? null,
      AnhNguoiGui: sender?.anhDaiDien ?? null,
    };
  }

  // Lấy lịch sử tin nhắn
  static async getByConversationId(conversationId, limit = 50, offset = 0) {
    // Bước 1: Lấy tin nhắn + dữ liệu reply thuần từ Chat DB
    const [rows] = await db.query(
      `SELECT m.*,
              reply.NoiDung as NoiDungTraLoi,
              reply.MaNguoiGui as MaNguoiGuiTraLoi
       FROM TinNhan m
       LEFT JOIN TinNhan reply ON m.TraLoiBoi = reply.MaTinNhan
       WHERE m.MaCuocTroChuyen = ? AND m.DaXoa = 0
       ORDER BY m.NgayGuiTinNhan DESC
       LIMIT ? OFFSET ?`,
      [conversationId, limit, offset]
    );

    if (rows.length === 0) return [];

    // Bước 2: Collect tất cả user ID cần tra cứu (người gửi + người gửi tin reply)
    const userIds = [
      ...rows.map(r => r.MaNguoiGui),
      ...rows.filter(r => r.MaNguoiGuiTraLoi).map(r => r.MaNguoiGuiTraLoi),
    ];
    const userMap = await userServiceClient.getUsersByIds(userIds);

    // Bước 3: Merge và đảo lại thứ tự (tin nhắn cũ lên trên)
    return rows.reverse().map(row => ({
      ...row,
      TenNguoiGui: userMap[row.MaNguoiGui]?.ten ?? null,
      HoNguoiGui: userMap[row.MaNguoiGui]?.ho ?? null,
      AnhNguoiGui: userMap[row.MaNguoiGui]?.anhDaiDien ?? null,
      TenNguoiTraLoi: row.MaNguoiGuiTraLoi
        ? (userMap[row.MaNguoiGuiTraLoi]?.ten ?? null)
        : null,
    }));
  }

  // Cập nhật tin nhắn
  static async update(messageId, noiDung) {
    await db.execute(
      `UPDATE TinNhan 
       SET NoiDung = ?, DaChinhSua = 1, SuaLuc = NOW()
       WHERE MaTinNhan = ?`,
      [noiDung, messageId]
    );
  }

  // Xóa tin nhắn (soft delete)
  static async delete(messageId) {
    await db.execute(
      `UPDATE TinNhan 
       SET DaXoa = 1, DaXoaLuc = NOW()
       WHERE MaTinNhan = ?`,
      [messageId]
    );
  }

  // Thêm / cập nhật reaction
  static async addReaction(messageId, userId, camXuc) {
    const [existing] = await db.execute(
      `SELECT Ma FROM CamXucTinNhan WHERE MaTinNhan = ? AND MaNguoiDung = ?`,
      [messageId, userId]
    );

    if (existing.length > 0) {
      await db.execute(
        `UPDATE CamXucTinNhan SET CamXuc = ?, NgayTao = NOW() WHERE Ma = ?`,
        [camXuc, existing[0].Ma]
      );
    } else {
      await db.execute(
        `INSERT INTO CamXucTinNhan (MaNguoiDung, MaTinNhan, CamXuc, NgayTao)
         VALUES (?, ?, ?, NOW())`,
        [userId, messageId, camXuc]
      );
    }
  }

  // Xóa reaction
  static async removeReaction(messageId, userId) {
    await db.execute(
      `DELETE FROM CamXucTinNhan WHERE MaTinNhan = ? AND MaNguoiDung = ?`,
      [messageId, userId]
    );
  }

  // Lấy reactions của tin nhắn
  static async getReactions(messageId) {
    // Bước 1: Lấy reactions thuần từ Chat DB
    const [rows] = await db.execute(
      `SELECT * FROM CamXucTinNhan WHERE MaTinNhan = ?`,
      [messageId]
    );

    if (rows.length === 0) return [];

    // Bước 2: Batch fetch user info
    const userIds = rows.map(r => r.MaNguoiDung);
    const userMap = await userServiceClient.getUsersByIds(userIds);

    // Bước 3: Merge
    return rows.map(row => ({
      ...row,
      Ten: userMap[row.MaNguoiDung]?.ten ?? null,
      ho: userMap[row.MaNguoiDung]?.ho ?? null,
      AnhDaiDien: userMap[row.MaNguoiDung]?.anhDaiDien ?? null,
    }));
  }

  // Đánh dấu đã đọc
  static async markAsRead(conversationId, userId) {
    await db.execute(
      `UPDATE NguoiThamGiaTroChuyen 
       SET LanCuoiXem = NOW()
       WHERE MaCuocTroChuyen = ? AND MaNguoiDung = ?`,
      [conversationId, userId]
    );
  }

// Batch load reactions cho nhiều tin nhắn cùng lúc — tránh N+1
// Thay thế cho việc gọi getReactions() trong vòng lặp
// Thêm vào class Message
static async getReactionsByMessageIds(messageIds) {
  if (!messageIds || messageIds.length === 0) return [];
 
  // Tạo placeholders (?): số lượng bằng messageIds.length
  const placeholders = messageIds.map(() => '?').join(',');
 
  const [rows] = await db.execute(
    `SELECT * FROM CamXucTinNhan WHERE MaTinNhan IN (${placeholders})`,
    messageIds
  );
 
  if (rows.length === 0) return [];
 
  // Batch fetch user info cho tất cả người đã react
  const userIds = rows.map(r => r.MaNguoiDung);
  const userMap = await userServiceClient.getUsersByIds(userIds);
 
  return rows.map(row => ({
    ...row,
    Ten:        userMap[row.MaNguoiDung]?.ten ?? null,
    ho:         userMap[row.MaNguoiDung]?.ho ?? null,
    AnhDaiDien: userMap[row.MaNguoiDung]?.anhDaiDien ?? null,
  }));
}
}

module.exports = Message;