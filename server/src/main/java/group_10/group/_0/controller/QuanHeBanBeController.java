package group_10.group._0.controller;

import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.BanBeResponse;
import group_10.group._0.service.QuanHeBanBeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ban-be")
@RequiredArgsConstructor
@Tag(name = "Bạn bè", description = "API quản lý quan hệ bạn bè")
public class QuanHeBanBeController {

    final QuanHeBanBeService service;

    @GetMapping("/{userId}")
    @Operation(summary = "Danh sách bạn bè", description = "Lấy danh sách bạn bè của user")
    public ApiResponse<List<BanBeResponse>> getFriends(@PathVariable Integer userId) {
        return ApiResponse.<List<BanBeResponse>>builder()
                .code(200)
                .data(service.getFriends(userId))
                .build();
    }

    @GetMapping("/check")
    @Operation(summary = "Kiểm tra bạn bè", description = "Kiểm tra 2 user có phải bạn bè không")
    public ApiResponse<Boolean> areFriends(
            @RequestParam Integer id1,
            @RequestParam Integer id2) {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .data(service.areFriends(id1, id2))
                .build();
    }

    @PostMapping("/add")
    @Operation(summary = "Thêm bạn bè", description = "Thêm quan hệ bạn bè giữa 2 user")
    public ApiResponse<Void> addFriend(
            @RequestParam Integer id1,
            @RequestParam Integer id2) {
        service.addFriend(id1, id2);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã thêm bạn bè thành công!")
                .build();
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Xóa bạn bè", description = "Xóa quan hệ bạn bè giữa 2 user")
    public ApiResponse<Void> removeFriend(
            @RequestParam Integer id1,
            @RequestParam Integer id2) {
        service.removeFriend(id1, id2);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã xóa bạn bè thành công!")
                .build();
    }

    @PostMapping("/by-ids")
    @Operation(summary = "Lấy thông tin bạn bè theo danh sách ID",
            description = "Truyền mảng ID user vào body, trả về danh sách thông tin user")
    public ApiResponse<List<BanBeResponse>> getFriendsByIds(
            @RequestBody List<Integer> ids) {
        return ApiResponse.<List<BanBeResponse>>builder()
                .code(200)
                .data(service.getFriendsByIds(ids))
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm bạn bè",
            description = "Tìm kiếm bạn bè theo họ, tên, biệt danh, email")
    public ApiResponse<List<BanBeResponse>> searchFriends(
            @RequestParam Integer userId,
            @RequestParam String query) {
        return ApiResponse.<List<BanBeResponse>>builder()
                .code(200)
                .data(service.searchFriends(userId, query))
                .build();
    }

}