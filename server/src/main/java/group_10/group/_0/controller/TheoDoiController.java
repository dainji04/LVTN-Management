package group_10.group._0.controller;

import group_10.group._0.dto.request.TheoDoiRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.SoLuongTheoDoiResponse;
import group_10.group._0.dto.response.TheoDoiResponse;
import group_10.group._0.entity.TheoDoi;
import group_10.group._0.service.TheoDoiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/theo-doi")
@Tag(name = "Theo dõi", description = "API quản lý hệ thống theo dõi người dùng, bao gồm tạo lượt theo dõi, hủy theo dõi và lấy thống kê")
@RequiredArgsConstructor
public class TheoDoiController {

    private final TheoDoiService theoDoiService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Theo dõi người dùng", description = "Tạo một lượt theo dõi mới và tự động gửi thông báo đến người được theo dõi")
    public ApiResponse<Void> createTheoDoi(@RequestBody TheoDoiRequest request) {
        theoDoiService.createTheoDoi(request);
        return ApiResponse.<Void>builder()
                .code(201)
                .message("Đã theo dõi người dùng thành công")
                .build();
    }

    @PostMapping("/khong-thong-bao")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Theo dõi ngầm (Không thông báo)", description = "Tạo một lượt theo dõi mới nhưng hệ thống sẽ không gửi thông báo cho người được theo dõi")
    public ApiResponse<TheoDoiResponse> createTheoDoiKhongThongBao(@RequestBody TheoDoiRequest request) {
        return ApiResponse.<TheoDoiResponse>builder()
                .code(201)
                .message("Đã theo dõi người dùng (không gửi thông báo)")
                .data(theoDoiService.createTheoDoiKhongThongBao(request))
                .build();
    }

    @GetMapping("/user/{maUser}")
    @Operation(summary = "Thống kê theo dõi của User", description = "Lấy tổng số lượng người đang theo dõi, người được theo dõi và danh sách chi tiết của một người dùng cụ thể")
    public ApiResponse<SoLuongTheoDoiResponse> soLuongFollow(@PathVariable Integer maUser) {
        return ApiResponse.<SoLuongTheoDoiResponse>builder()
                .code(200)
                .message("Lấy thông tin thống kê theo dõi thành công")
                .data(theoDoiService.soLuongFollow(maUser))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết lượt theo dõi", description = "Lấy thông tin chi tiết của một lượt theo dõi dựa trên ID của lượt theo dõi đó")
    public ApiResponse<TheoDoi> getTheoDoiByID(@PathVariable Integer id) {
        return ApiResponse.<TheoDoi>builder()
                .code(200)
                .message("Lấy chi tiết lượt theo dõi thành công")
                .data(theoDoiService.getTheoDoiByID(id))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hủy theo dõi", description = "Xóa một lượt theo dõi hiện có khỏi hệ thống dựa vào ID")
    public ApiResponse<Void> xoaTheoDoi(@PathVariable Integer id) {
        theoDoiService.xoaTheoDoi(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã hủy theo dõi thành công")
                .build();
    }
}