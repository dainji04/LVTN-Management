package group_10.group._0.controller;

import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.UsersResponse;
import group_10.group._0.service.AuthenticationService;
import group_10.group._0.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
@Tag(name = "Info user", description = "API hiển thị thông user thông qua token")
public class ThongTinUserController {

    final UsersService usersService;
    final AuthenticationService authService;

    //login
    @GetMapping("")
    @Operation(summary = "Thông tin user", description = "Lấy thông tin user qua token đang có sẵn")
    private ApiResponse<UsersResponse> thongTinTaiKhoan(){
        return ApiResponse.<UsersResponse>builder()
                .data(usersService.getMyInfo())
                .build();
    }
}