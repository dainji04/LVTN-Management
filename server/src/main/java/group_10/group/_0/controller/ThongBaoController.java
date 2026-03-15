package group_10.group._0.controller;


import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.ThongBaoResponse;
import group_10.group._0.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/thongbao")
@RequiredArgsConstructor
public class ThongBaoController {

    private final ThongBaoService thongBaoService;

    // 1. Lấy danh sách thông báo theo User ID
    @GetMapping("/user/{userId}")
    public ApiResponse<List<ThongBaoResponse>> getByUser(@PathVariable Integer userId) {
        return ApiResponse.<List<ThongBaoResponse>>builder()
                .code(200)
                .message("Lấy danh sách thông báo thành công")
                .data(thongBaoService.layDSThongBao(userId))
                .build();
    }

    // 2. Tạo mới thông báo (Thường gọi nội bộ hoặc từ Client hành động)
    @PostMapping
    public ApiResponse<ThongBaoResponse> create(@RequestBody ThongBaoRequest request) {
        return ApiResponse.<ThongBaoResponse>builder()
                .code(201) // 201 Created
                .message("Tạo thông báo thành công")
                .data(thongBaoService.taoMoiTHongBao(request))
                .build();
    }

    // 3. Đánh dấu đã đọc (Sử dụng PatchMapping cho việc cập nhật một phần)
    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Integer id) {
        thongBaoService.docThongBao(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã đánh dấu là đã đọc")
                .build();
    }

    // 4. Xóa thông báo
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        thongBaoService.xoaThongBao(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa thông báo thành công")
                .build();
    }
}
