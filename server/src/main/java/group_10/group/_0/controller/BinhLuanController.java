package group_10.group._0.controller;

import group_10.group._0.dto.request.BinhLuanRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.BinhLuanResponse;
import group_10.group._0.dto.response.SliceResponse;
import group_10.group._0.service.BinhLuanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/binh-luan")
@RequiredArgsConstructor
@Tag(name = "Bình luận", description = "API quản lý bình luận bài viết")
public class BinhLuanController {

    final BinhLuanService binhLuanService;

    @PostMapping
    @Operation(summary = "Tạo bình luận")
    public ApiResponse<BinhLuanResponse> create(@RequestBody BinhLuanRequest request) {
        return ApiResponse.<BinhLuanResponse>builder()
                .code(201)
                .data(binhLuanService.createBinhLuan(request))
                .build();
    }

    @GetMapping("/bai-viet/{maBaiDang}")
    @Operation(summary = "Lấy bình luận theo bài viết")
    public ApiResponse<SliceResponse<BinhLuanResponse>> getByBaiViet(
            @PathVariable Integer maBaiDang,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<SliceResponse<BinhLuanResponse>>builder()
                .code(200)
                .data(binhLuanService.getByBaiViet(maBaiDang, page, size))
                .build();
    }

    @GetMapping("/replies/{maBinhLuanCha}")
    @Operation(summary = "Lấy reply của bình luận")
    public ApiResponse<SliceResponse<BinhLuanResponse>> getReplies(
            @PathVariable Integer maBinhLuanCha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<SliceResponse<BinhLuanResponse>>builder()
                .code(200)
                .data(binhLuanService.getReplies(maBinhLuanCha, page, size))
                .build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Cập nhật bình luận")
    public ApiResponse<BinhLuanResponse> update(
            @PathVariable Integer id,
            @RequestParam String noiDung) {
        return ApiResponse.<BinhLuanResponse>builder()
                .code(200)
                .data(binhLuanService.updateBinhLuan(id, noiDung))
                .build();
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa bình luận")
    public ApiResponse<Void> delete(
            @PathVariable Integer id,
            @RequestParam Integer nguoiXoaId) {
        binhLuanService.deleteBinhLuan(id, nguoiXoaId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa bình luận thành công!")
                .build();
    }
}