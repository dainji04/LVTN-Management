package group_10.group._0.controller;

import com.nimbusds.jose.JOSEException;
import group_10.group._0.dto.request.IntrospectRequest;
import group_10.group._0.dto.request.LogoutRequest;
import group_10.group._0.dto.request.RefreshRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.AuthenticationResponse;
import group_10.group._0.dto.response.IntrospectResponse;
import group_10.group._0.dto.response.LoiMoiResponse;
import group_10.group._0.service.AuthenticationService;
import group_10.group._0.service.LoiMoiKetBanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/loi-moi-ket-ban")
@RequiredArgsConstructor
@Tag(name = "Lời mời kết bạn", description = "API quản lý lời mời kết bạn")
public class LoiMoiKetBanController {

    final LoiMoiKetBanService service;

    @PostMapping("/gui")
    @Operation(summary = "Gửi lời mời kết bạn")
    public ApiResponse<LoiMoiResponse> guiLoiMoi(
            @RequestParam Integer nguoiGuiId,
            @RequestParam Integer nguoiNhanId) {
        return ApiResponse.<LoiMoiResponse>builder()
                .code(200)
                .message("Đã gửi lời mời kết bạn!")
                .data(service.guiLoiMoi(nguoiGuiId, nguoiNhanId))
                .build();
    }

    @GetMapping("/{nguoiNhanId}")
    @Operation(summary = "Danh sách lời mời nhận được")
    public ApiResponse<List<LoiMoiResponse>> danhSachLoiMoi(
            @PathVariable Integer nguoiNhanId) {
        return ApiResponse.<List<LoiMoiResponse>>builder()
                .code(200)
                .data(service.danhSachLoiMoi(nguoiNhanId))
                .build();
    }

    @PatchMapping("/{id}/trang-thai")
    @Operation(summary = "Cập nhật trạng thái lời mời")
    public ApiResponse<LoiMoiResponse> capNhatTrangThai(
            @PathVariable("id") Integer loiMoiId,
            @RequestParam String trangThai) {
        return ApiResponse.<LoiMoiResponse>builder()
                .code(200)
                .data(service.capNhatTrangThai(loiMoiId, trangThai))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa lời mời kết bạn")
    public ApiResponse<Void> xoaLoiMoi(@PathVariable Integer id) {
        service.xoaLoiMoi(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã xóa lời mời!")
                .build();
    }
}
