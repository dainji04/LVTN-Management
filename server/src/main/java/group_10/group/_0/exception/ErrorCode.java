package group_10.group._0.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // ==========================================
    // 1. SYSTEM & COMMON ERRORS (99xx)
    // ==========================================
    UNCATEGORIZED(9999, "Lỗi hệ thống chưa phân loại", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(9998, "Key cấu hình không hợp lệ", HttpStatus.BAD_REQUEST),
    PAYLOAD_TOO_LARGE(9001, "Dung lượng file vượt quá giới hạn", HttpStatus.PAYLOAD_TOO_LARGE),
    DATA_EXISTED(9002, "Dữ liệu đã tồn tại", HttpStatus.CONFLICT),
    REQUEST_IS_PROCESSED(9003, "Yêu cầu đã được xử lý", HttpStatus.CONFLICT),

    // ==========================================
    // 2. AUTHENTICATION & AUTHORIZATION (10xx)
    // ==========================================
    UNAUTHENTICATED(1001, "Bạn chưa đăng nhập", HttpStatus.UNAUTHORIZED),
    SAI_MAT_KHAU(1002, "Email hoặc mật khẩu không chính xác", HttpStatus.UNAUTHORIZED), // Có thể đổi tên thành CREDENTIALS_INVALID
    UNAUTHORIZED(1003, "Bạn không có quyền thực hiện hành động này", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1004, "Không có quyền truy cập tài nguyên", HttpStatus.FORBIDDEN),

    // ==========================================
    // 3. VALIDATION ERRORS (20xx)
    // ==========================================
    FULL_NAME_INVALID(2001, "Họ và tên không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(2002, "Email không hợp lệ", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(2003, "Số điện thoại không hợp lệ (Phải có 10 số, bắt đầu bằng số 0)", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(2004, "Mật khẩu phải từ 8 đến 50 ký tự", HttpStatus.BAD_REQUEST),
    ADDRESS_INVALID(2005, "Địa chỉ không được để trống", HttpStatus.BAD_REQUEST),
    VAITRO_INVALID(2006, "Vai trò không được để trống", HttpStatus.BAD_REQUEST),
    IDVAITRO_EMPTY(2007, "ID vai trò không được để trống", HttpStatus.BAD_REQUEST),
    TRANGTHAI_INVALID(2008, "Trạng thái hoạt động không được để trống", HttpStatus.BAD_REQUEST),

    // ==========================================
    // 4. USER DOMAIN (30xx)
    // ==========================================
    USER_NOT_EXISTED(3001, "User không tồn tại", HttpStatus.NOT_FOUND),
    USER_EXISTED(3002, "User đã tồn tại", HttpStatus.CONFLICT),
    EMAIL_IS_EXISTED(3003, "Email đã tồn tại", HttpStatus.CONFLICT),

    // ==========================================
    // 5. SOCIAL DOMAIN (POST, COMMENT, GROUP) (40xx)
    // ==========================================
    BAIVIET_NOT_EXISTED(4001, "Bài viết không tồn tại", HttpStatus.NOT_FOUND),
    BINHLUAN_NOT_EXISTED(4002, "Bình luận không tồn tại", HttpStatus.NOT_FOUND),
    BINHLUANCHA_NOT_EXISTED(4003, "Bình luận cha không tồn tại", HttpStatus.NOT_FOUND),
    GROUP_NOT_EXISTED(4004, "Nhóm không tồn tại", HttpStatus.NOT_FOUND),
    MEMBER_NOT_EXISTED(4005, "Thành viên không tồn tại", HttpStatus.NOT_FOUND),
    FOLLOW_NOT_EXISTED(4006, "Chưa tồn tại theo dõi", HttpStatus.NOT_FOUND),

    // ==========================================
    // 6. NOTIFICATION & OTHERS (50xx)
    // ==========================================
    NOTIFICATION_NOT_EXISTED(5001, "Mã thông báo không tồn tại", HttpStatus.NOT_FOUND),
    ACCESS_NOT_EXISTED(5002, "Yêu cầu không tồn tại", HttpStatus.NOT_FOUND),
    FOLLOW_IS_EXISTED(3003, "đã theo dõi", HttpStatus.CONFLICT);




    private int code;
    private String message;
    private HttpStatus status;
    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
