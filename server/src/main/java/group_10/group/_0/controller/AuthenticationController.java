package group_10.group._0.controller;

import com.nimbusds.jose.JOSEException;
import group_10.group._0.dto.request.AuthenticationRequest;
import group_10.group._0.dto.request.IntrospectRequest;
import group_10.group._0.dto.request.LogoutRequest;
import group_10.group._0.dto.request.RefreshRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.AuthenticationResponse;
import group_10.group._0.dto.response.IntrospectResponse;
import group_10.group._0.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "API xác thực người dùng (đăng nhập, token, logout)")
public class AuthenticationController {
    @Autowired
    AuthenticationService service;



    //    Kiểm tra token
    @PostMapping("/introspect")
    @Operation(summary = "Kiểm tra token", description = "Kiểm tra JWT token có hợp lệ hay không")
    ApiResponse<IntrospectResponse> kiemTraToken(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = service.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .data(result)
                .build();
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất", description = "Hủy JWT token hiện tại, thêm vào danh sách token không hợp lệ")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        service.logout(request);
        return ApiResponse.<Void>builder()
                .message("Da logout")
                .build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới token", description = "Dùng refresh token để lấy JWT token mới")
    ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = service.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .data(result)
                .build();
    }
}