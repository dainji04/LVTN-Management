package group_10.group._0.controller;

import group_10.group._0.dto.request.BaiVietRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.BaiVietResponse;
import group_10.group._0.service.BaiVietService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bai-viet")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Bài Viết", description = "API quản lý bài viết (tạo, xem, cập nhật, xóa)")
public class BaiVietController {

    BaiVietService baiVietService;

    // Lấy tất cả bài viết
    @GetMapping
    @Operation(summary = "Lấy tất cả bài viết", description = "Trả về danh sách toàn bộ bài viết kèm ảnh")
    public ApiResponse<List<BaiVietResponse>> getAllBaiViet() {
        return ApiResponse.<List<BaiVietResponse>>builder()
                .code(200)
                .data(baiVietService.getAllBaiViet())
                .build();
    }

    // Lấy bài viết theo ID
    @GetMapping("/{id}")
    @Operation(summary = "Lấy bài viết theo ID", description = "Trả về chi tiết 1 bài viết kèm danh sách ảnh")
    public ApiResponse<BaiVietResponse> getBaiVietById(
            @Parameter(description = "ID của bài viết") @PathVariable Integer id) {
        return ApiResponse.<BaiVietResponse>builder()
                .code(200)
                .data(baiVietService.getBaiVietById(id))
                .build();
    }

    // Lấy tất cả bài viết của 1 user
    @GetMapping("/user/{maNguoiDung}")
    @Operation(summary = "Lấy bài viết theo user", description = "Trả về tất cả bài viết của 1 người dùng")
    public ApiResponse<List<BaiVietResponse>> getBaiVietByUser(
            @Parameter(description = "ID của người dùng") @PathVariable Integer maNguoiDung) {
        return ApiResponse.<List<BaiVietResponse>>builder()
                .code(200)
                .data(baiVietService.getBaiVietByUser(maNguoiDung))
                .build();
    }

    // Tạo bài viết mới
    @PostMapping
    @Operation(summary = "Tạo bài viết mới", description = "Tạo bài viết mới kèm danh sách ảnh (nếu có)")
    public ResponseEntity<ApiResponse<BaiVietResponse>> createBaiViet(@RequestBody @Valid BaiVietRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<BaiVietResponse>builder()
                        .code(201)
                        .data(baiVietService.createBaiViet(request))
                        .build()
        );
    }

    // Cập nhật bài viết
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật bài viết", description = "Cập nhật nội dung và ảnh của bài viết, ảnh cũ sẽ bị thay thế")
    public ApiResponse<BaiVietResponse> updateBaiViet(
            @Parameter(description = "ID của bài viết cần cập nhật") @PathVariable Integer id,
            @RequestBody @Valid BaiVietRequest request) {
        return ApiResponse.<BaiVietResponse>builder()
                .code(200)
                .data(baiVietService.updateBaiViet(id, request))
                .build();
    }

    // Xóa bài viết
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa bài viết", description = "Xóa bài viết và toàn bộ ảnh liên quan")
    public ApiResponse<Void> deleteBaiViet(
            @Parameter(description = "ID của bài viết cần xóa") @PathVariable Integer id) {
        baiVietService.deleteBaiViet(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa bài viết thành công")
                .build();
    }
}