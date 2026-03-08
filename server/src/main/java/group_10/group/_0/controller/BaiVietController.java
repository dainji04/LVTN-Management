package group_10.group._0.controller;

import group_10.group._0.dto.request.BaiVietRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.BaiVietResponse;
import group_10.group._0.service.BaiVietService;
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
public class BaiVietController {

    BaiVietService baiVietService;

    // Lấy tất cả bài viết
    @GetMapping
    public ApiResponse<List<BaiVietResponse>> getAllBaiViet() {
        return ApiResponse.<List<BaiVietResponse>>builder()
                .code(200)
                .data(baiVietService.getAllBaiViet())
                .build();
    }

    // Lấy bài viết theo ID
    @GetMapping("/{id}")
    public ApiResponse<BaiVietResponse> getBaiVietById(@PathVariable Integer id) {
        return ApiResponse.<BaiVietResponse>builder()
                .code(200)
                .data(baiVietService.getBaiVietById(id))
                .build();
    }

    // Lấy tất cả bài viết của 1 user
    @GetMapping("/user/{maNguoiDung}")
    public ApiResponse<List<BaiVietResponse>> getBaiVietByUser(@PathVariable Integer maNguoiDung) {
        return ApiResponse.<List<BaiVietResponse>>builder()
                .code(200)
                .data(baiVietService.getBaiVietByUser(maNguoiDung))
                .build();
    }

    // Tạo bài viết mới
    @PostMapping
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
    public ApiResponse<BaiVietResponse> updateBaiViet(
            @PathVariable Integer id,
            @RequestBody @Valid BaiVietRequest request) {
        return ApiResponse.<BaiVietResponse>builder()
                .code(200)
                .data(baiVietService.updateBaiViet(id, request))
                .build();
    }

    // Xóa bài viết
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBaiViet(@PathVariable Integer id) {
        baiVietService.deleteBaiViet(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa bài viết thành công")
                .build();
    }
}