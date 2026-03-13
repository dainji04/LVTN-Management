const db = require('../config/cloud.config.js');

class Message {
  // Tạo tin nhắn mới
  static async create({ 
    maCuocTroChuyen, 
    maNguoiGui, 
    noiDung, 
    loaiTinNhan = 'text',
    duongDanFile = null,
    tenFile = null,
    kichThuocFile = null,
    traLoiBoi = null,
    chuyenTiepTu = null
  }) {
    const [result] = await db.execute(
      `INSERT INTO TinNhan (
        MaCuocTroChuyen, MaNguoiGui, NoiDung, LoaiTinNhan, 
        DuongDanFile, TenFile, KichThuocFile, TraLoiBoi, 
        ChuyenTiepTu, DaXoa, DaChinhSua, NgayGuiTinNhan
      )
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, NOW())`,
      [
        maCuocTroChuyen, maNguoiGui, noiDung, loaiTinNhan,
        duongDanFile, tenFile, kichThuocFile, traLoiBoi,
        chuyenTiepTu
      ]
    );
    return result.insertId;
  }

  // Lấy tin nhắn theo ID
  static async getById(messageId) {
    const [rows] = await db.execute(
      `SELECT m.*, 
              u.Ten as TenNguoiGui,
              u.ho as HoNguoiGui,
              u.AnhDaiDien as AnhNguoiGui
       FROM TinNhan m
       JOIN Users u ON m.MaNguoiGui = u.MaNguoiDung
       WHERE m.MaTinNhan = ? AND m.DaXoa = 0`,
      [messageId]
    );
    return rows[0];
  }

  // Lấy lịch sử tin nhắn
  static async getByConversationId(conversationId, limit = 50, offset = 0) {
    const [rows] = await db.query(
      `SELECT m.*, 
              u.Ten as TenNguoiGui,
              u.ho as HoNguoiGui,
              u.AnhDaiDien as AnhNguoiGui,
              reply.NoiDung as NoiDungTraLoi,
              replyUser.Ten as TenNguoiTraLoi
       FROM TinNhan m
       JOIN Users u ON m.MaNguoiGui = u.MaNguoiDung
       LEFT JOIN TinNhan reply ON m.TraLoiBoi = reply.MaTinNhan
       LEFT JOIN Users replyUser ON reply.MaNguoiGui = replyUser.MaNguoiDung
       WHERE m.MaCuocTroChuyen = ? AND m.DaXoa = 0
       ORDER BY m.NgayGuiTinNhan DESC
       LIMIT ? OFFSET ?`,
      [conversationId, limit, offset]
    );
    return rows.reverse(); // Đảo ngược để tin nhắn cũ lên trên
  }

  // Cập nhật tin nhắn
  static async update(messageId, noiDung) {
    await db.execute(
      `UPDATE TinNhan 
       SET NoiDung = ?, 
           DaChinhSua = 1, 
           SuaLuc = NOW()
       WHERE MaTinNhan = ?`,
      [noiDung, messageId]
    );
  }

  // Xóa tin nhắn (soft delete)
  static async delete(messageId) {
    await db.execute(
      `UPDATE TinNhan 
       SET DaXoa = 1, 
           DaXoaLuc = NOW()
       WHERE MaTinNhan = ?`,
      [messageId]
    );
  }

  // Thêm reaction vào tin nhắn
  static async addReaction(messageId, userId, camXuc) {
    // Kiểm tra xem đã react chưa
    const [existing] = await db.execute(
      `SELECT Ma FROM CamXucTinNhan 
       WHERE MaTinNhan = ? AND MaNguoiDung = ?`,
      [messageId, userId]
    );

    if (existing.length > 0) {
      // Cập nhật reaction
      await db.execute(
        `UPDATE CamXucTinNhan 
         SET CamXuc = ?, NgayTao = NOW()
         WHERE Ma = ?`,
        [camXuc, existing[0].Ma]
      );
    } else {
      // Thêm reaction mới
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
      `DELETE FROM CamXucTinNhan 
       WHERE MaTinNhan = ? AND MaNguoiDung = ?`,
      [messageId, userId]
    );
  }

  // Lấy reactions của tin nhắn
  static async getReactions(messageId) {
    const [rows] = await db.execute(
      `SELECT c.*, u.Ten, u.ho, u.AnhDaiDien
       FROM CamXucTinNhan c
       JOIN Users u ON c.MaNguoiDung = u.MaNguoiDung
       WHERE c.MaTinNhan = ?`,
      [messageId]
    );
    return rows;
  }

  // Đánh dấu tin nhắn đã đọc
  static async markAsRead(conversationId, userId) {
    await db.execute(
      `UPDATE NguoiThamGiaTroChuyen 
       SET LanCuoiXem = NOW()
       WHERE MaCuocTroChuyen = ? AND MaNguoiDung = ?`,
      [conversationId, userId]
    );
  }
}

module.exports = Message;
