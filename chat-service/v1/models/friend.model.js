const db = require('../config/cloud.config.js');

class Friend {
  // Kiểm tra 2 user có phải bạn bè không
  static async areFriends(userId1, userId2) {
    const [rows] = await db.execute(
      `SELECT 1 FROM QuanHeBanBe 
       WHERE (MaNguoiDung1 = ? AND MaNguoiDung2 = ?)
          OR (MaNguoiDung1 = ? AND MaNguoiDung2 = ?)`,
      [userId1, userId2, userId2, userId1]
    );
    return rows.length > 0;
  }

  // Lấy danh sách bạn bè của user
  static async getFriends(userId) {
    const [rows] = await db.execute(
      `SELECT 
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.MaNguoiDung
          ELSE u1.MaNguoiDung
        END as MaNguoiDung,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.ho
          ELSE u1.ho
        END as ho,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.Ten
          ELSE u1.Ten
        END as Ten,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.BietDanh
          ELSE u1.BietDanh
        END as BietDanh,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.AnhDaiDien
          ELSE u1.AnhDaiDien
        END as AnhDaiDien,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.Email
          ELSE u1.Email
        END as Email,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.HoatDongLanCuoi
          ELSE u1.HoatDongLanCuoi
        END as HoatDongLanCuoi,
        qh.NgayTao as NgayKetBan
       FROM QuanHeBanBe qh
       LEFT JOIN Users u1 ON qh.MaNguoiDung1 = u1.MaNguoiDung
       LEFT JOIN Users u2 ON qh.MaNguoiDung2 = u2.MaNguoiDung
       WHERE qh.MaNguoiDung1 = ? OR qh.MaNguoiDung2 = ?
       ORDER BY NgayKetBan DESC`,
      [userId, userId, userId, userId, userId, userId, userId, userId, userId]
    );
    return rows;
  }

  // Tìm kiếm bạn bè theo tên
  static async searchFriends(userId, query) {
    const [rows] = await db.execute(
      `SELECT 
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.MaNguoiDung
          ELSE u1.MaNguoiDung
        END as MaNguoiDung,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.ho
          ELSE u1.ho
        END as ho,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.Ten
          ELSE u1.Ten
        END as Ten,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.BietDanh
          ELSE u1.BietDanh
        END as BietDanh,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.AnhDaiDien
          ELSE u1.AnhDaiDien
        END as AnhDaiDien,
        CASE 
          WHEN qh.MaNguoiDung1 = ? THEN u2.Email
          ELSE u1.Email
        END as Email
       FROM QuanHeBanBe qh
       LEFT JOIN Users u1 ON qh.MaNguoiDung1 = u1.MaNguoiDung
       LEFT JOIN Users u2 ON qh.MaNguoiDung2 = u2.MaNguoiDung
       WHERE (qh.MaNguoiDung1 = ? OR qh.MaNguoiDung2 = ?)
       HAVING (ho LIKE ? OR Ten LIKE ? OR BietDanh LIKE ? OR Email LIKE ?)
       LIMIT 20`,
      [userId, userId, userId, userId, userId, userId, 
       userId, userId,
       `%${query}%`, `%${query}%`, `%${query}%`, `%${query}%`]
    );
    return rows;
  }

//   // Thêm bạn bè mới (sau khi chấp nhận lời mời kết bạn)
//   static async addFriend(userId1, userId2) {
//     // Đảm bảo userId nhỏ hơn luôn là MaNguoiDung1 để tránh duplicate
//     const [user1, user2] = userId1 < userId2 ? [userId1, userId2] : [userId2, userId1];
    
//     const [result] = await db.execute(
//       `INSERT INTO QuanHeBanBe (MaNguoiDung1, MaNguoiDung2, NgayTao, NgayCapNhat)
//        VALUES (?, ?, NOW(), NOW())
//        ON DUPLICATE KEY UPDATE NgayCapNhat = NOW()`,
//       [user1, user2]
//     );
//     return result.insertId;
//   }

  // Xóa bạn bè
  static async removeFriend(userId1, userId2) {
    await db.execute(
      `DELETE FROM QuanHeBanBe 
       WHERE (MaNguoiDung1 = ? AND MaNguoiDung2 = ?)
          OR (MaNguoiDung1 = ? AND MaNguoiDung2 = ?)`,
      [userId1, userId2, userId2, userId1]
    );
  }

  // Lấy danh sách bạn bè chung giữa 2 user
  static async getMutualFriends(userId1, userId2) {
    const [rows] = await db.execute(
      `SELECT DISTINCT
        CASE 
          WHEN qh1.MaNguoiDung1 = ? THEN qh1.MaNguoiDung2
          ELSE qh1.MaNguoiDung1
        END as MaNguoiDung,
        u.ho, u.Ten, u.AnhDaiDien
       FROM QuanHeBanBe qh1
       JOIN QuanHeBanBe qh2 ON (
         (qh1.MaNguoiDung1 = qh2.MaNguoiDung1 OR qh1.MaNguoiDung1 = qh2.MaNguoiDung2 OR
          qh1.MaNguoiDung2 = qh2.MaNguoiDung1 OR qh1.MaNguoiDung2 = qh2.MaNguoiDung2)
         AND qh2.MaNguoiDung1 != ? AND qh2.MaNguoiDung2 != ?
       )
       JOIN Users u ON u.MaNguoiDung = CASE 
         WHEN qh1.MaNguoiDung1 = ? THEN qh1.MaNguoiDung2
         ELSE qh1.MaNguoiDung1
       END
       WHERE (qh1.MaNguoiDung1 = ? OR qh1.MaNguoiDung2 = ?)
         AND (qh2.MaNguoiDung1 = ? OR qh2.MaNguoiDung2 = ?)`,
      [userId1, userId1, userId1, userId1, userId1, userId1, userId2, userId2]
    );
    return rows;
  }
}

module.exports = Friend;
