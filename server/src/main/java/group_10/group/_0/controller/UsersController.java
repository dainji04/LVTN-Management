package group_10.group._0.controller;

import group_10.group._0.dto.UsersRequest;
import group_10.group._0.dto.UsersResponse;
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
    public ResponseEntity<List<UsersResponse>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    public ResponseEntity<UsersResponse> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(usersService.getUserById(id));
    }

    // Đăng ký user mới
    @PostMapping("/register")
    public ResponseEntity<UsersResponse> register(@RequestBody @Valid UsersRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.createUser(request));
    }

    // Cập nhật user
    @PutMapping("/{id}")
    public ResponseEntity<UsersResponse> updateUser(
            @PathVariable Integer id,
            @RequestBody @Valid UsersRequest request) {
        return ResponseEntity.ok(usersService.updateUser(id, request));
    }

    // Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}