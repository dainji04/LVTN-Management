// models/Conversation.js  —  Chat Service
// Đã xóa toàn bộ JOIN với bảng Users.
// Thông tin user được lấy qua userServiceClient (gọi User Service API).

const db = require('../config/cloud.config.js');
const userServiceClient = require('../helpers/userServiceClient.js');

class Conversation {
  // Tạo cuộc trò chuyện mới
  static async create({ ten, anhDaiDien, maNguoiTao }) {
    const [result] = await db.execute(
      `INSERT INTO CuocTroChuyen (Ten, AnhDaiDien, MaNguoiTao, SoLuongThanhVien, SoLuongTinNhan, NgayTao, NgayCapNhat)
       VALUES (?, ?, ?, 0, 0, NOW(), NOW())`,
      [ten, anhDaiDien, maNguoiTao]
    );
    return result.insertId;
  }

  // Lấy danh sách cuộc trò chuyện của user
  static async getByUserId(userId) {
    // Bước 1: Lấy dữ liệu thuần từ Chat DB (không JOIN Users)
    const [rows] = await db.execute(
      `SELECT c.*,
              CASE 
                WHEN c.SoLuongThanhVien = 2 THEN 'direct'
                ELSE 'group'
              END as LoaiCuocTroChuyen,
              m.NoiDung as TinNhanCuoi,
              m.NgayGuiTinNhan as NgayTinNhanCuoi,
              m.MaNguoiGui as MaNguoiGuiCuoi
       FROM CuocTroChuyen c
       LEFT JOIN TinNhan m ON c.MaTinNhanCuoi = m.MaTinNhan
       WHERE c.MaCuocTroChuyen IN (
         SELECT MaCuocTroChuyen FROM NguoiThamGiaTroChuyen WHERE MaNguoiDung = ?
       )
       ORDER BY COALESCE(m.NgayGuiTinNhan, c.NgayCapNhat) DESC`,
      [userId]
    );

    if (rows.length === 0) return [];

    // Bước 2: Thu thập tất cả user ID cần tra cứu (MaNguoiTao + MaNguoiGuiCuoi)
    const userIds = [
      ...rows.map(r => r.MaNguoiTao),
      ...rows.filter(r => r.MaNguoiGuiCuoi).map(r => r.MaNguoiGuiCuoi),
    ];
    const userMap = await userServiceClient.getUsersByIds(userIds);

    // Bước 3: Gắn thông tin user vào kết quả
    return rows.map(row => ({
      ...row,
      TenNguoiTao: userMap[row.MaNguoiTao]?.Ten ?? null,
      AnhNguoiTao: userMap[row.MaNguoiTao]?.AnhDaiDien ?? null,
    }));
  }

  // Lấy danh sách cuộc trò chuyện 1-1
  static async getDirectConversations(userId) {
    // Bước 1: Lấy conversation + ID của người còn lại
    const [rows] = await db.execute(
      `SELECT c.*,
              m.NoiDung as TinNhanCuoi,
              m.NgayGuiTinNhan as NgayTinNhanCuoi,
              m.MaNguoiGui as MaNguoiGuiCuoi,
              ntg2.MaNguoiDung as FriendId
       FROM CuocTroChuyen c
       JOIN NguoiThamGiaTroChuyen ntg1 ON c.MaCuocTroChuyen = ntg1.MaCuocTroChuyen AND ntg1.MaNguoiDung = ?
       JOIN NguoiThamGiaTroChuyen ntg2 ON c.MaCuocTroChuyen = ntg2.MaCuocTroChuyen AND ntg2.MaNguoiDung != ?
       LEFT JOIN TinNhan m ON c.MaTinNhanCuoi = m.MaTinNhan
       WHERE c.SoLuongThanhVien = 2
       ORDER BY COALESCE(m.NgayGuiTinNhan, c.NgayCapNhat) DESC`,
      [userId, userId]
    );

    if (rows.length === 0) return [];

    // Bước 2: Batch fetch thông tin tất cả friend
    const friendIds = rows.map(r => r.FriendId);
    const userMap = await userServiceClient.getUsersByIds(friendIds);

    // Bước 3: Merge
    return rows.map(row => ({
      ...row,
      FriendHo: userMap[row.FriendId]?.ho ?? null,
      FriendTen: userMap[row.FriendId]?.ten ?? null,
      FriendAvatar: userMap[row.FriendId]?.anhDaiDien ?? null,
      FriendLastActive: userMap[row.FriendId]?.hoatDongLanCuoi ?? null,
      type: "direct",
    }));
  }

  // Lấy danh sách nhóm
  static async getGroupConversations(userId) {
    const [rows] = await db.execute(
      `SELECT c.*,
              m.NoiDung as TinNhanCuoi,
              m.NgayGuiTinNhan as NgayTinNhanCuoi,
              m.MaNguoiGui as MaNguoiGuiCuoi
       FROM CuocTroChuyen c
       LEFT JOIN TinNhan m ON c.MaTinNhanCuoi = m.MaTinNhan
       WHERE c.MaCuocTroChuyen IN (
         SELECT MaCuocTroChuyen FROM NguoiThamGiaTroChuyen WHERE MaNguoiDung = ?
       )
       AND c.SoLuongThanhVien > 2
       ORDER BY COALESCE(m.NgayGuiTinNhan, c.NgayCapNhat) DESC`,
      [userId]
    );

    if (rows.length === 0) return [];

    const userIds = [
      ...rows.map(r => r.MaNguoiTao),
      ...rows.filter(r => r.MaNguoiGuiCuoi).map(r => r.MaNguoiGuiCuoi),
    ];
    const userMap = await userServiceClient.getUsersByIds(userIds);

    return rows.map(row => ({
      ...row,
      TenNguoiTao: userMap[row.MaNguoiTao]?.ten ?? null,
      AnhNguoiTao: userMap[row.MaNguoiTao]?.anhDaiDien ?? null,
    }));
  }

  // Lấy chi tiết cuộc trò chuyện
  static async getById(conversationId) {
    const [rows] = await db.execute(
      `SELECT c.*,
              CASE 
                WHEN c.SoLuongThanhVien = 2 THEN 'direct'
                ELSE 'group'
              END as LoaiCuocTroChuyen
       FROM CuocTroChuyen c
       WHERE c.MaCuocTroChuyen = ?`,
      [conversationId]
    );
    if (!rows[0]) return null;

    const creator = await userServiceClient.getUserById(rows[0].MaNguoiTao);
    return {
      ...rows[0],
      TenNguoiTao: creator?.Ten ?? null,
      AnhNguoiTao: creator?.AnhDaiDien ?? null,
    };
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
  // Trả về mảng [{ MaNguoiDung, VaiTro, NgayThamGia, DuocThemBoi, ...userInfo }]
  static async getMembers(conversationId) {
    const [rows] = await db.execute(
      `SELECT MaNguoiDung, VaiTro, NgayThamGia, DuocThemBoi
       FROM NguoiThamGiaTroChuyen
       WHERE MaCuocTroChuyen = ?`,
      [conversationId]
    );

    if (rows.length === 0) return [];

    const allIds = [
      ...rows.map(r => r.MaNguoiDung),
      ...rows.filter(r => r.DuocThemBoi).map(r => r.DuocThemBoi),
    ];
    const userMap = await userServiceClient.getUsersByIds(allIds);

    return rows.map(row => ({
      ...row,
      Ten: userMap[row.MaNguoiDung]?.ten ?? null,
      ho: userMap[row.MaNguoiDung]?.ho ?? null,
      AnhDaiDien: userMap[row.MaNguoiDung]?.anhDaiDien ?? null,
      HoatDongLanCuoi: userMap[row.MaNguoiDung]?.hoatDongLanCuoi ?? null,
    }));
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
    const existing = await this.findDirectConversation(userId1, userId2);
    if (existing) return existing.MaCuocTroChuyen;

    const conversationId = await this.create({
      ten: null,  
      anhDaiDien: null,
      maNguoiTao: userId1,
    });

    await this.addMember(conversationId, userId1, null, 0);
    await this.addMember(conversationId, userId2, null, 0);

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

  // Tìm kiếm nhóm theo tên (chỉ trong Chat DB, không cần User Service)
// Thêm vào class Conversation
static async searchGroups(userId, keyword) {
  const [rows] = await db.execute(
    `SELECT c.*,
            m.NoiDung as TinNhanCuoi,
            m.NgayGuiTinNhan as NgayTinNhanCuoi
     FROM CuocTroChuyen c
     LEFT JOIN TinNhan m ON c.MaTinNhanCuoi = m.MaTinNhan
     WHERE c.SoLuongThanhVien > 2
       AND c.Ten LIKE ?
       AND c.MaCuocTroChuyen IN (
         SELECT MaCuocTroChuyen FROM NguoiThamGiaTroChuyen WHERE MaNguoiDung = ?
       )
     ORDER BY COALESCE(m.NgayGuiTinNhan, c.NgayCapNhat) DESC
     LIMIT 20`,
    [`%${keyword}%`, userId]
  );
 
  if (rows.length === 0) return [];
 
  // Lấy tên người tạo nhóm
  const creatorIds = rows.map(r => r.MaNguoiTao);
  const userMap = await userServiceClient.getUsersByIds(creatorIds);
 
  return rows.map(row => ({
    ...row,
    TenNguoiTao: userMap[row.MaNguoiTao]?.ten ?? null,
    AnhNguoiTao: userMap[row.MaNguoiTao]?.anhDaiDien ?? null,
  }));
}
 
}

module.exports = Conversation;