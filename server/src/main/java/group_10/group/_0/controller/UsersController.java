package group_10.group._0.controller;

import group_10.group._0.dto.request.UsersRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.UsersResponse;
import group_10.group._0.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API quản lý người dùng (đăng ký, xem, cập nhật, xóa)")
public class UsersController {

    final UsersService usersService;

    // Lấy tất cả users
    @GetMapping
    @Operation(summary = "Lấy tất cả người dùng", description = "Trả về danh sách toàn bộ người dùng")
    public ApiResponse<List<UsersResponse>> getAllUsers() {
        return ApiResponse.<List<UsersResponse>>builder()
//                .code(200)
                .data(usersService.getAllUsers())
                .build();
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    @Operation(summary = "Lấy người dùng theo ID", description = "Trả về thông tin chi tiết của 1 người dùng")
    public ApiResponse<UsersResponse> getUserById(
            @Parameter(description = "ID của người dùng") @PathVariable Integer id) {
        return ApiResponse.<UsersResponse>builder()
                .data(usersService.getUserById(id))
                .build();
    }

    // Đăng ký user mới
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Đăng ký tài khoản", description = "Tạo tài khoản người dùng mới")
    public ApiResponse<UsersResponse> register(@RequestBody @Valid UsersRequest request) {
        return ApiResponse.<UsersResponse>builder()
                .code(201)
                .data(usersService.createUser(request))
                .build();
    }

    // Cập nhật user
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật người dùng", description = "Cập nhật thông tin profile của người dùng")
    public ApiResponse<UsersResponse> updateUser(
            @Parameter(description = "ID của người dùng cần cập nhật") @PathVariable Integer id,
            @RequestBody @Valid UsersRequest request) {
        return ApiResponse.<UsersResponse>builder()
                .message("Da update user co id: "+id)
                .data(usersService.updateUser(id, request))
                .build();
    }

    // Xóa user
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Xóa người dùng", description = "Xóa tài khoản người dùng khỏi hệ thống")
    public ApiResponse<Void> deleteUser(
            @Parameter(description = "ID của người dùng cần xóa") @PathVariable Integer id) {
        usersService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .message("Da xoa user")
                .build();
    }
}