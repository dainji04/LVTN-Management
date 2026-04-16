package group_10.group._0.controller;

import group_10.group._0.dto.request.TheoDoiNhomRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.TheoDoiNhomResponse;
import group_10.group._0.service.TheoDoiNhomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/theo-doi-nhom")
@Tag(name = "Theo dõi Nhóm", description = "API quản lý hệ thống theo dõi nhóm, bao gồm tạo lượt theo dõi, và hủy theo dõi nhóm độc lập")
@RequiredArgsConstructor
public class TheoDoiNhomController {

    private final TheoDoiNhomService theoDoiNhomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Theo dõi nhóm", description = "Tạo một lượt theo dõi nhóm mới (có thể dùng khi user bấm theo dõi chay không cần join group)")
    public ApiResponse<TheoDoiNhomResponse> createTheoDoiNhom(@RequestBody TheoDoiNhomRequest request) {
        return ApiResponse.<TheoDoiNhomResponse>builder()
                .code(201)
                .message("Đã theo dõi nhóm thành công")
                .data(theoDoiNhomService.createTheoDoiNhom(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hủy theo dõi nhóm", description = "Xóa một lượt theo dõi nhóm hiện có dựa vào ID")
    public ApiResponse<Void> xoaTheoDoiNhom(@PathVariable Integer id) {
        theoDoiNhomService.xoaTheoDoiNhom(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã hủy theo dõi nhóm thành công")
                .build();
    }
}
