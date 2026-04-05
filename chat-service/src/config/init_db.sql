-- ============================================================
-- Chat Service Database
-- Tách từ SocialMedia (SocialMedia_Gr10)
-- ============================================================
-- Các bảng: CuocTroChuyen, TinNhan, NguoiThamGiaTroChuyen, CamXucTinNhan
--
-- LƯU Ý MICROSERVICES:
--   Tất cả FOREIGN KEY tham chiếu tới bảng Users đã được XÓA.
--   Cột MaNguoiDung (INT) vẫn được giữ lại và đánh index để
--   truy vấn hiệu quả.
--   Việc validate user hợp lệ phải thực hiện qua REST API / gRPC
--   của User Service trước khi ghi dữ liệu vào Chat Service.
-- ============================================================

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- -----------------------------------------------------------
-- 1. CuocTroChuyen (Conversations)
--    MaNguoiTao: ID người tạo (validate qua User Service API)
--    MaTinNhanCuoi: FK nội bộ tới TinNhan (giữ nguyên)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `CuocTroChuyen`;
CREATE TABLE `CuocTroChuyen` (
  `MaCuocTroChuyen` int NOT NULL AUTO_INCREMENT,
  `Ten`             varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `AnhDaiDien`      varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MaNguoiTao`      int NOT NULL COMMENT 'User ID - validate qua User Service',
  `SoLuongThanhVien` int DEFAULT '0',
  `SoLuongTinNhan`  int DEFAULT '0',
  `NgayTao`         datetime DEFAULT NULL,
  `NgayCapNhat`     datetime DEFAULT NULL,
  `MaTinNhanCuoi`   int DEFAULT NULL,
  PRIMARY KEY (`MaCuocTroChuyen`),
  KEY `idx_CTC_Creator`  (`MaNguoiTao`),
  KEY `idx_CTC_LastMsg`  (`MaTinNhanCuoi`)
  -- FK tới TinNhan được thêm sau khi tạo bảng TinNhan (xem bên dưới)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 2. TinNhan (Messages)
--    MaNguoiGui: ID người gửi (validate qua User Service API)
--    TraLoiBoi / ChuyenTiepTu: FK nội bộ tới chính bảng TinNhan
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `TinNhan`;
CREATE TABLE `TinNhan` (
  `MaTinNhan`       int NOT NULL AUTO_INCREMENT,
  `MaCuocTroChuyen` int NOT NULL,
  `MaNguoiGui`      int NOT NULL COMMENT 'User ID - validate qua User Service',
  `NoiDung`         varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `LoaiTinNhan`     varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DuongDanFile`    varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `TenFile`         varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `KichThuocFile`   varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ThoiLuong`       int DEFAULT NULL,
  `TraLoiBoi`       int DEFAULT NULL,
  `ChuyenTiepTu`    int DEFAULT NULL,
  `DaXoa`           bit(1) DEFAULT b'0',
  `DaXoaLuc`        datetime DEFAULT NULL,
  `DaChinhSua`      bit(1) DEFAULT b'0',
  `SuaLuc`          datetime DEFAULT NULL,
  `NgayGuiTinNhan`  datetime DEFAULT NULL,
  PRIMARY KEY (`MaTinNhan`),
  KEY `idx_TinNhan_Sender`  (`MaNguoiGui`),
  KEY `idx_TinNhan_ReplyTo` (`TraLoiBoi`),
  KEY `idx_TinNhan_Fwd`     (`ChuyenTiepTu`),
  KEY `idx_TinNhan_Convo`   (`MaCuocTroChuyen`),
  CONSTRAINT `fk_TinNhan_Convo`   FOREIGN KEY (`MaCuocTroChuyen`) REFERENCES `CuocTroChuyen` (`MaCuocTroChuyen`),
  CONSTRAINT `fk_TinNhan_ReplyTo` FOREIGN KEY (`TraLoiBoi`)       REFERENCES `TinNhan` (`MaTinNhan`),
  CONSTRAINT `fk_TinNhan_Fwd`     FOREIGN KEY (`ChuyenTiepTu`)    REFERENCES `TinNhan` (`MaTinNhan`)
  -- FK tới Users đã bị XÓA (cross-service)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Thêm FK từ CuocTroChuyen tới TinNhan (sau khi TinNhan đã được tạo)
ALTER TABLE `CuocTroChuyen`
  ADD CONSTRAINT `fk_CTC_LastMsg` FOREIGN KEY (`MaTinNhanCuoi`) REFERENCES `TinNhan` (`MaTinNhan`);

-- -----------------------------------------------------------
-- 3. NguoiThamGiaTroChuyen (Conversation Participants)
--    MaNguoiDung / DuocThemBoi: ID user (validate qua User Service API)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `NguoiThamGiaTroChuyen`;
CREATE TABLE `NguoiThamGiaTroChuyen` (
  `Ma`              int NOT NULL AUTO_INCREMENT,
  `MaCuocTroChuyen` int NOT NULL,
  `MaNguoiDung`     int NOT NULL COMMENT 'User ID - validate qua User Service',
  `VaiTro`          int DEFAULT NULL,
  `BietDanh`        varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `TatThongBao`     bit(1) DEFAULT b'0',
  `TinNhanDaXem`    bit(1) DEFAULT b'0',
  `LanCuoiXem`      datetime DEFAULT NULL,
  `NgayThamGia`     datetime DEFAULT NULL,
  `DuocThemBoi`     int DEFAULT NULL COMMENT 'User ID - validate qua User Service',
  PRIMARY KEY (`Ma`),
  KEY `idx_NTGTC_Convo`   (`MaCuocTroChuyen`),
  KEY `idx_NTGTC_User`    (`MaNguoiDung`),
  KEY `idx_NTGTC_AddedBy` (`DuocThemBoi`),
  CONSTRAINT `fk_NTGTC_Convo` FOREIGN KEY (`MaCuocTroChuyen`) REFERENCES `CuocTroChuyen` (`MaCuocTroChuyen`)
  -- FK tới Users (MaNguoiDung, DuocThemBoi) đã bị XÓA (cross-service)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------
-- 4. CamXucTinNhan (Message Reactions)
--    MaNguoiDung: ID user (validate qua User Service API)
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `CamXucTinNhan`;
CREATE TABLE `CamXucTinNhan` (
  `Ma`          int NOT NULL AUTO_INCREMENT,
  `MaNguoiDung` int NOT NULL COMMENT 'User ID - validate qua User Service',
  `MaTinNhan`   int NOT NULL,
  `CamXuc`      varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NgayTao`     datetime DEFAULT NULL,
  PRIMARY KEY (`Ma`),
  KEY `idx_CXTinNhan_User`    (`MaNguoiDung`),
  KEY `idx_CXTinNhan_Message` (`MaTinNhan`),
  CONSTRAINT `fk_CXTinNhan_Message` FOREIGN KEY (`MaTinNhan`) REFERENCES `TinNhan` (`MaTinNhan`)
  -- FK tới Users đã bị XÓA (cross-service)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;