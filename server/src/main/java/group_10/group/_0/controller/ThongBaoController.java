package group_10.group._0.controller;


import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.ThongBaoResponse;
import group_10.group._0.service.SseService;
import group_10.group._0.service.ThongBaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/thongbao")
@Tag(name = "Thông báo", description = "API quản lý thông báo của hệ thống, bao gồm việc tạo, lấy danh sách, đánh dấu đã đọc và xóa thông báo")
@RequiredArgsConstructor
public class ThongBaoController {

    private final ThongBaoService thongBaoService;
    private final SseService sseService;



    // Endpoint để client đăng ký nhận thông báo qua SSE
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Đăng ký nhận thông báo", description = "Client gọi endpoint này để đăng ký nhận thông báo qua SSE, luôn gọi khi user đăng nhập và giữ kết nối mở để nhận thông báo thời gian thực")
    public SseEmitter subscribe(@PathVariable Integer userId) {
        return sseService.subscribe(userId);
    }

    @PostMapping("/loi-moi-ket-ban")
    @Operation(summary = "Gửi lời mời kết bạn", description = "Gửi lời mời kết bạn thông qua thông báo")
    public ApiResponse<ThongBaoResponse> guiLoiMoiKetBan(
            @RequestParam Integer nguoiGuiId,
            @RequestParam Integer nguoiNhanId) {
        ThongBaoRequest request = ThongBaoRequest.builder()
                .maNguoiHanhDong(nguoiGuiId)
                .maNguoiNhan(nguoiNhanId)
                .loaiHanhDong("LOI_MOI_KET_BAN")
                .build();
        return ApiResponse.<ThongBaoResponse>builder()
                .code(200)
                .message("Đã gửi lời mời kết bạn!")
                .data(thongBaoService.taoMoiThongBao(request))
                .build();
    }


    // 1. Lấy danh sách thông báo theo User ID
    @GetMapping("/user/{userId}")
    @Operation(summary = "Danh sách thông báo", description = "Lấy danh sách thông báo của một user, có thể phân trang và lọc theo trạng thái đã đọc/chưa đọc") // Có thể thêm các tham số query để hỗ trợ phân trang và lọc
    public ApiResponse<List<ThongBaoResponse>> getByUser(@PathVariable Integer userId) {
        return ApiResponse.<List<ThongBaoResponse>>builder()
                .code(200)
                .message("Lấy danh sách thông báo thành công")
                .data(thongBaoService.layDSThongBao(userId))
                .build();
    }

    // 2. Tạo mới thông báo (Thường gọi nội bộ hoặc từ Client hành động)
    @PostMapping
    @Operation(summary = "Tạo thông báo mới", description = "Tạo một thông báo mới, thường được gọi nội bộ khi có sự kiện cần thông báo hoặc từ client khi user thực hiện hành động nào đó") // Có thể thêm các tham số để xác định loại thông báo, nội dung, v.v.
    public ApiResponse<ThongBaoResponse> create(@RequestBody ThongBaoRequest request) {
        return ApiResponse.<ThongBaoResponse>builder()
                .code(201) // 201 Created
                .message("Tạo thông báo thành công")
                .data(thongBaoService.taoMoiThongBao(request))
                .build();
    }

    // 3. Đánh dấu đã đọc (Sử dụng PatchMapping cho việc cập nhật một phần)
    @PatchMapping("/{id}/read")
    @Operation(summary = "Đánh dấu thông báo đã đọc", description = "Đánh dấu một thông báo là đã đọc, thường được gọi khi user xem thông báo đó") // Có thể thêm tham số để đánh dấu tất cả thông báo của user là đã đọc
    public ApiResponse<Void> markAsRead(@PathVariable Integer id) {
        thongBaoService.docThongBao(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã đánh dấu là đã đọc")
                .build();
    }

    // 4. Xóa thông báo
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thông báo", description = "Xóa một thông báo, thường được gọi khi user muốn xóa thông báo đó khỏi danh sách") // Có thể thêm tham số để xóa tất cả thông báo đã đọc của user
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        thongBaoService.xoaThongBao(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa thông báo thành công")
                .build();
    }


}
