package group_10.group._0.controller;

import group_10.group._0.dto.request.ThanhVien_GroupRequest;
import group_10.group._0.dto.request.ThanhVien_GroupUpdateRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.ThanhVien_GroupResponse;
import group_10.group._0.service.ThanhVien_GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/thanhvien-group")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Thành viên Nhóm", description = "API quản lý thành viên trong nhóm")
public class ThanhVien_GroupController {

    ThanhVien_GroupService thanhVienGroupService;

    @PostMapping
    @Operation(summary = "Thêm thành viên", description = "Thêm người dùng mới vào nhóm")
    public ApiResponse<ThanhVien_GroupResponse> createThanhVien(@RequestBody ThanhVien_GroupRequest request) {
        return ApiResponse.<ThanhVien_GroupResponse>builder()
                .code(200)
                .data(thanhVienGroupService.createThanhVien(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa thành viên", description = "Xóa một thành viên khỏi nhóm")
    public ApiResponse<Void> deleteThanhVien(@PathVariable Integer id) {
        thanhVienGroupService.deleteThanhVien(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa thành viên thành công")
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thành viên", description = "Cập nhật vai trò thành viên trong nhóm")
    public ApiResponse<ThanhVien_GroupResponse> updateThanhVien(@PathVariable Integer id, @RequestBody ThanhVien_GroupUpdateRequest request) {
        return ApiResponse.<ThanhVien_GroupResponse>builder()
                .code(200)
                .data(thanhVienGroupService.updateThanhVien(request, id))
                .build();
    }

    @GetMapping("/group/{idGroup}")
    @Operation(summary = "Danh sách thành viên", description = "Lên danh sách tất cả thành viên của một nhóm")
    public ApiResponse<List<ThanhVien_GroupResponse>> getDachSachThanhVien(@PathVariable Integer idGroup) {
        return ApiResponse.<List<ThanhVien_GroupResponse>>builder()
                .code(200)
                .data(thanhVienGroupService.DSThanhVien(idGroup))
                .build();
    }

    @GetMapping("/group/{idGroup}/count")
    @Operation(summary = "Đếm số thành viên", description = "Lấy số lượng thành viên trong một nhóm")
    public ApiResponse<Long> getSoThanhVienTrongGroup(@PathVariable Integer idGroup) {
        return ApiResponse.<Long>builder()
                .code(200)
                .data(thanhVienGroupService.soThanhVienTrongGroup(idGroup))
                .build();
    }
}
