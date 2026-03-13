const db = require('../config/cloud.config.js');

class Conversation {
  // Tạo cuộc trò chuyện mới
  static async create({ ten, anhDaiDien, maNguoiTao, loai = 'group' }) {
    const [result] = await db.execute(
      `INSERT INTO CuocTroChuyen (Ten, AnhDaiDien, MaNguoiTao, SoLuongThanhVien, SoLuongTinNhan, NgayTao, NgayCapNhat)
       VALUES (?, ?, ?, 1, 0, NOW(), NOW())`,
      [ten, anhDaiDien, maNguoiTao]
    );
    return result.insertId;
  }

  // Lấy danh sách cuộc trò chuyện của user (bao gồm cả 1-1 và nhóm)
  static async getByUserId(userId) {
    const [rows] = await db.execute(
      `SELECT c.*, 
              u.Ten as TenNguoiTao,
              u.AnhDaiDien as AnhNguoiTao,
              m.NoiDung as TinNhanCuoi,
              m.NgayGuiTinNhan as NgayTinNhanCuoi,
              m.MaNguoiGui as MaNguoiGuiCuoi,
              CASE 
                WHEN c.SoLuongThanhVien = 2 THEN 'direct'
                ELSE 'group'
              END as LoaiCuocTroChuyen
       FROM CuocTroChuyen c
       LEFT JOIN Users u ON c.MaNguoiTao = u.MaNguoiDung
       LEFT JOIN TinNhan m ON c.MaTinNhanCuoi = m.MaTinNhan
       WHERE c.MaCuocTroChuyen IN (
         SELECT MaCuocTroChuyen FROM NguoiThamGiaTroChuyen WHERE MaNguoiDung = ?
       )
       ORDER BY COALESCE(m.NgayGuiTinNhan, c.NgayCapNhat) DESC`,
      [userId]
    );
    return rows;
  }

  // Lấy danh sách cuộc trò chuyện 1-1 với bạn bè
  static async getDirectConversations(userId) {
    const [rows] = await db.execute(
      `SELECT c.*, 
              m.NoiDung as TinNhanCuoi,
              m.NgayGuiTinNhan as NgayTinNhanCuoi,
              m.MaNguoiGui as MaNguoiGuiCuoi,
              other_user.MaNguoiDung as FriendId,
              other_user.ho as FriendHo,
              other_user.Ten as FriendTen,
              other_user.AnhDaiDien as FriendAvatar,
              other_user.HoatDongLanCuoi as FriendLastActive
       FROM CuocTroChuyen c
       JOIN NguoiThamGiaTroChuyen ntg1 ON c.MaCuocTroChuyen = ntg1.MaCuocTroChuyen AND ntg1.MaNguoiDung = ?
       JOIN NguoiThamGiaTroChuyen ntg2 ON c.MaCuocTroChuyen = ntg2.MaCuocTroChuyen AND ntg2.MaNguoiDung != ?
       JOIN Users other_user ON ntg2.MaNguoiDung = other_user.MaNguoiDung
       LEFT JOIN TinNhan m ON c.MaTinNhanCuoi = m.MaTinNhan
       WHERE c.SoLuongThanhVien = 2
       ORDER BY COALESCE(m.NgayGuiTinNhan, c.NgayCapNhat) DESC`,
      [userId, userId]
    );
    return rows;
  }

  // Lấy danh sách nhóm
  static async getGroupConversations(userId) {
    const [rows] = await db.execute(
      `SELECT c.*, 
              u.Ten as TenNguoiTao,
              u.AnhDaiDien as AnhNguoiTao,
              m.NoiDung as TinNhanCuoi,
              m.NgayGuiTinNhan as NgayTinNhanCuoi,
              m.MaNguoiGui as MaNguoiGuiCuoi
       FROM CuocTroChuyen c
       LEFT JOIN Users u ON c.MaNguoiTao = u.MaNguoiDung
       LEFT JOIN TinNhan m ON c.MaTinNhanCuoi = m.MaTinNhan
       WHERE c.MaCuocTroChuyen IN (
         SELECT MaCuocTroChuyen FROM NguoiThamGiaTroChuyen WHERE MaNguoiDung = ?
       )
       AND c.SoLuongThanhVien > 2
       ORDER BY COALESCE(m.NgayGuiTinNhan, c.NgayCapNhat) DESC`,
      [userId]
    );
    return rows;
  }

  // Lấy chi tiết cuộc trò chuyện
  static async getById(conversationId) {
    const [rows] = await db.execute(
      `SELECT c.*, 
              u.Ten as TenNguoiTao,
              u.AnhDaiDien as AnhNguoiTao,
              CASE 
                WHEN c.SoLuongThanhVien = 2 THEN 'direct'
                ELSE 'group'
              END as LoaiCuocTroChuyen
       FROM CuocTroChuyen c
       LEFT JOIN Users u ON c.MaNguoiTao = u.MaNguoiDung
       WHERE c.MaCuocTroChuyen = ?`,
      [conversationId]
    );
    return rows[0];
  }

  // Cập nhật số lượng tin nhắn
  static async updateMessageCount(conversationId, lastMessageId) {
    await db.execute(
      `UPDATE CuocTroChuyen 
       SET SoLuongTinNhan = SoLuongTinNhan + 1,
           MaTinNhanCuoi = ?,
           NgayCapNhat = NOW()
       WHERE MaCuocTroChuyen = ?`,
      [lastMessageId, conversationId]
    );
  }

  // Lấy danh sách thành viên
  static async getMembers(conversationId) {
    const [rows] = await db.execute(
      `SELECT u.MaNguoiDung, u.Ten, u.ho, u.AnhDaiDien, u.HoatDongLanCuoi,
              ntg.VaiTro, ntg.NgayThamGia, ntg.DuocThemBoi
       FROM NguoiThamGiaTroChuyen ntg
       JOIN Users u ON ntg.MaNguoiDung = u.MaNguoiDung
       WHERE ntg.MaCuocTroChuyen = ?`,
      [conversationId]
    );
    return rows;
  }

  // Thêm thành viên vào cuộc trò chuyện
  static async addMember(conversationId, userId, addedBy = null, vaiTro = 0) {
    await db.execute(
      `INSERT INTO NguoiThamGiaTroChuyen (MaCuocTroChuyen, MaNguoiDung, VaiTro, NgayThamGia, DuocThemBoi)
       VALUES (?, ?, ?, NOW(), ?)`,
      [conversationId, userId, vaiTro, addedBy]
    );
    
    await db.execute(
      `UPDATE CuocTroChuyen 
       SET SoLuongThanhVien = SoLuongThanhVien + 1
       WHERE MaCuocTroChuyen = ?`,
      [conversationId]
    );
  }

  // Kiểm tra user có trong cuộc trò chuyện không
  static async isMember(conversationId, userId) {
    const [rows] = await db.execute(
      `SELECT 1 FROM NguoiThamGiaTroChuyen 
       WHERE MaCuocTroChuyen = ? AND MaNguoiDung = ?`,
      [conversationId, userId]
    );
    return rows.length > 0;
  }

  // Tìm cuộc trò chuyện 1-1 giữa 2 người
  static async findDirectConversation(userId1, userId2) {
    const [rows] = await db.execute(
      `SELECT c.MaCuocTroChuyen
       FROM CuocTroChuyen c
       WHERE c.SoLuongThanhVien = 2
       AND c.MaCuocTroChuyen IN (
         SELECT MaCuocTroChuyen FROM NguoiThamGiaTroChuyen WHERE MaNguoiDung = ?
       )
       AND c.MaCuocTroChuyen IN (
         SELECT MaCuocTroChuyen FROM NguoiThamGiaTroChuyen WHERE MaNguoiDung = ?
       )
       LIMIT 1`,
      [userId1, userId2]
    );
    return rows[0];
  }

  // Tạo cuộc trò chuyện 1-1 với bạn bè
  static async createDirectConversation(userId1, userId2, friendName) {
    // Kiểm tra xem đã có cuộc trò chuyện chưa
    const existing = await this.findDirectConversation(userId1, userId2);
    if (existing) {
      return existing.MaCuocTroChuyen;
    }

    // Tạo mới
    const conversationId = await this.create({
      ten: friendName,
      anhDaiDien: null,
      maNguoiTao: userId1
    });

    // Thêm cả 2 người vào
    await this.addMember(conversationId, userId1, null, 0);
    await this.addMember(conversationId, userId2, userId1, 0);

    return conversationId;
  }

  // Xóa thành viên khỏi nhóm
  static async removeMember(conversationId, userId) {
    await db.execute(
      `DELETE FROM NguoiThamGiaTroChuyen 
       WHERE MaCuocTroChuyen = ? AND MaNguoiDung = ?`,
      [conversationId, userId]
    );
    
    await db.execute(
      `UPDATE CuocTroChuyen 
       SET SoLuongThanhVien = SoLuongThanhVien - 1
       WHERE MaCuocTroChuyen = ?`,
      [conversationId]
    );
  }
}

module.exports = Conversation;
