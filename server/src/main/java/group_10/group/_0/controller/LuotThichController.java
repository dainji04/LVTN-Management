package group_10.group._0.controller;

import group_10.group._0.dto.request.LuotThichRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.LuotThichResponse;
import group_10.group._0.service.LuotThichService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/luot-thich")
@RequiredArgsConstructor
@Tag(name = "Lượt thích", description = "API quản lý lượt thích bài viết và bình luận")
public class LuotThichController {

    final LuotThichService luotThichService;

    @PostMapping("/toggle")
    @Operation(summary = "Toggle thích/bỏ thích")
    public ApiResponse<Boolean> toggleLike(@RequestBody LuotThichRequest request) {
        boolean liked = luotThichService.toggleLike(request);
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(liked ? "Đã thích!" : "Đã bỏ thích!")
                .data(liked)
                .build();
    }

    @GetMapping("/check")
    @Operation(summary = "Kiểm tra đã thích chưa")
    public ApiResponse<Boolean> daThich(
            @RequestParam Integer maNguoiDung,
            @RequestParam Integer maDoiTuong,
            @RequestParam String loaiDoiTuong) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .data(luotThichService.daThich(maNguoiDung, maDoiTuong, loaiDoiTuong))
                .build();
    }

    @GetMapping("/danh-sach")
    @Operation(summary = "Danh sách người thích")
    public ApiResponse<List<LuotThichResponse>> danhSach(
            @RequestParam Integer maDoiTuong,
            @RequestParam String loaiDoiTuong) {
        return ApiResponse.<List<LuotThichResponse>>builder()
                .code(200)
                .data(luotThichService.danhSachLuotThich(maDoiTuong, loaiDoiTuong))
                .build();
    }
}