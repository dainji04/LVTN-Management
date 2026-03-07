package group_10.group._0.controller;

import group_10.group._0.dto.request.UsersRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.UsersResponse;
import group_10.group._0.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    final UsersService usersService;

    // Lấy tất cả users
    @GetMapping
    public ApiResponse<List<UsersResponse>> getAllUsers() {
        return ApiResponse.<List<UsersResponse>>builder()
//                .code(200)
                .data(usersService.getAllUsers())
                .build();
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    public ApiResponse<UsersResponse> getUserById(@PathVariable Integer id) {
        return ApiResponse.<UsersResponse>builder()
                .data(usersService.getUserById(id))
                .build();
    }

    // Đăng ký user mới
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UsersResponse> register(@RequestBody @Valid UsersRequest request) {
        return ApiResponse.<UsersResponse>builder()
                .code(201)
                .data(usersService.createUser(request))
                .build();
    }

    // Cập nhật user
    @PutMapping("/{id}")
    public ApiResponse<UsersResponse> updateUser(
            @PathVariable Integer id,
            @RequestBody @Valid UsersRequest request) {
        return ApiResponse.<UsersResponse>builder()
                .message("Da update user co id: "+id)
                .data(usersService.updateUser(id, request))
                .build();
    }

    // Xóa user
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteUser(@PathVariable Integer id) {
        usersService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .message("Da xoa user")
                .build();
    }
}