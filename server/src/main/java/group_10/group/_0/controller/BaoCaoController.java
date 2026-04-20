package group_10.group._0.controller;

import group_10.group._0.dto.request.BaoCaoRequest;
import group_10.group._0.dto.request.BaoCaoUpdateRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.BaoCaoResponse;
import group_10.group._0.service.BaoCaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Report", description = "API quản lý báo cáo (tạo mới, xử lý báo cáo dành cho admin)")
public class BaoCaoController {

    BaoCaoService baoCaoService;

    @PostMapping
    @Operation(summary = "Tạo báo cáo mới", description = "Người dùng gửi báo cáo về một đối tượng (bài viết, nhóm, người dùng)")
    public ApiResponse<BaoCaoResponse> createBaoCao(@RequestBody BaoCaoRequest request, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ApiResponse.<BaoCaoResponse>builder()
                .code(200)
                .data(baoCaoService.createBaoCao(request, jwtToken))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả báo cáo (Admin)", description = "Lấy danh sách toàn bộ báo cáo hiện có")
    public ApiResponse<List<BaoCaoResponse>> getAllBaoCao(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ApiResponse.<List<BaoCaoResponse>>builder()
                .code(200)
                .data(baoCaoService.getAllBaoCao(jwtToken))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật trạng thái báo cáo (Admin)", description = "Admin xử lý báo cáo (duyệt, từ chối, bỏ qua)")
    public ApiResponse<BaoCaoResponse> updateTrangThaiBaoCao(@PathVariable Integer id, @RequestBody BaoCaoUpdateRequest request, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ApiResponse.<BaoCaoResponse>builder()
                .code(200)
                .data(baoCaoService.updateTrangThaiBaoCao(id, request, jwtToken))
                .build();
    }

    @GetMapping("/status")
    @Operation(summary = "Lấy báo cáo theo trạng thái (Admin)", description = "Lọc danh sách báo cáo theo trạng thái")
    public ApiResponse<List<BaoCaoResponse>> getBaoCaoByTrangThai(@RequestParam String trangThai, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ApiResponse.<List<BaoCaoResponse>>builder()
                .code(200)
                .data(baoCaoService.getBaoCaoByTrangThai(trangThai, jwtToken))
                .build();
    }

    @GetMapping("/target")
    @Operation(summary = "Lấy báo cáo theo đối tượng (Admin)", description = "Lấy danh sách các báo cáo nhắm vào một đối tượng cụ thể")
    public ApiResponse<List<BaoCaoResponse>> getBaoCaoByDoiTuong(@RequestParam String loaiDoiTuong, @RequestParam Integer idDoiTuong, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ApiResponse.<List<BaoCaoResponse>>builder()
                .code(200)
                .data(baoCaoService.getBaoCaoByDoiTuong(loaiDoiTuong, idDoiTuong, jwtToken))
                .build();
    }

    @PutMapping("/unlock")
    @Operation(summary = "Mở khoá đối tượng (Admin)",
            description = "Admin mở khoá bài viết / nhóm / người dùng đã bị cấm. Tham số: loaiDoiTuong (BAI_VIET | NHOM | NGUOI_DUNG), idDoiTuong")
    public ApiResponse<Void> moKhoaDoiTuong(@RequestParam String loaiDoiTuong, @RequestParam Integer idDoiTuong,
                                             @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        baoCaoService.moKhoaDoiTuong(loaiDoiTuong, idDoiTuong, jwtToken);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã mở khoá đối tượng thành công")
                .build();
    }
}
