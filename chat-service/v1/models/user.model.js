const db = require('../config/cloud.config.js');

class User {
  // Lấy thông tin user theo ID
  static async getById(userId) {
    const [rows] = await db.execute(
      `SELECT MaNguoiDung, ho, Ten, BietDanh, AnhDaiDien, AnhNen, 
              Email, SoDienThoai, HoatDongLanCuoi
       FROM Users 
       WHERE MaNguoiDung = ?`,
      [userId]
    );
    return rows[0];
  }

  // Lấy user theo email
  static async getByEmail(email) {
    const [rows] = await db.execute(
      `SELECT * FROM Users WHERE Email = ?`,
      [email]
    );
    return rows[0];
  }

  // Cập nhật hoạt động lần cuối
  static async updateLastActivity(userId) {
    await db.execute(
      `UPDATE Users 
       SET HoatDongLanCuoi = NOW()
       WHERE MaNguoiDung = ?`,
      [userId]
    );
  }

  // Tìm kiếm users
  static async search(query, excludeUserId = null) {
    let sql = `SELECT MaNguoiDung, ho, Ten, BietDanh, AnhDaiDien, Email
               FROM Users 
               WHERE (ho LIKE ? OR Ten LIKE ? OR Email LIKE ? OR BietDanh LIKE ?)`;
    
    const params = [`%${query}%`, `%${query}%`, `%${query}%`, `%${query}%`];
    
    if (excludeUserId) {
      sql += ` AND MaNguoiDung != ?`;
      params.push(excludeUserId);
    }
    
    sql += ` LIMIT 20`;
    
    const [rows] = await db.execute(sql, params);
    return rows;
  }

  // Kiểm tra xem có bị chặn không
  static async isBlocked(userId, targetUserId) {
    const [rows] = await db.execute(
      `SELECT 1 FROM Chan 
       WHERE (MaNguoiChan = ? AND MaNguoiBiChan = ?)
          OR (MaNguoiChan = ? AND MaNguoiBiChan = ?)`,
      [userId, targetUserId, targetUserId, userId]
    );
    return rows.length > 0;
  }
}

module.exports = User;
