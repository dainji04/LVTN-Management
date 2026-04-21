package group_10.group._0.controller;

import group_10.group._0.dto.request.GroupRequest;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.BaiVietResponse;
import group_10.group._0.dto.response.GroupResponse;
import group_10.group._0.entity.Nhom;
import group_10.group._0.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Group", description = "API quản lý nhóm (tạo mới, cập nhật, xóa)")
public class GroupController {

    GroupService groupService;

    @PostMapping
    @Operation(summary = "Tạo nhóm mới", description = "Tạo nhóm mới và thêm người tạo làm ADMIN")
    public ApiResponse<GroupResponse> createGroup(@RequestBody GroupRequest request, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ApiResponse.<GroupResponse>builder()
                .code(200)
                .data(groupService.createGroup(request, jwtToken))
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật nhóm", description = "Cập nhật thông tin của nhóm (tên, mô tả, ảnh, loại,...)")
    public ApiResponse<GroupResponse> updateGroup(@RequestBody GroupRequest request, @PathVariable Integer id) {
        return ApiResponse.<GroupResponse>builder()
                .code(200)
                .data(groupService.updateGroup(request, id))
                .build();
    }

    @DeleteMapping("/{idGroup}")
    @Operation(summary = "Xóa nhóm", description = "Xóa nhóm bao gồm tất cả thành viên, bài viết và yêu cầu tham gia")
    public ApiResponse<Void> deleteGroup(@PathVariable Integer idGroup, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        groupService.deleteGroup(idGroup, jwtToken);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa nhóm thành công")
                .build();
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm nhóm", description = "Tìm kiếm danh sách nhóm dựa theo tên")
    public ApiResponse<List<GroupResponse>> findGroup(@RequestParam("key") String key) {
        return ApiResponse.<List<GroupResponse>>builder()
                .code(200)
                .data(groupService.findGroup(key))
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin nhóm", description = "Lấy thông tin chi tiết của một nhóm dựa vào ID")
    public ApiResponse<GroupResponse> getGroupById(@PathVariable("id") Integer id) {
        return ApiResponse.<GroupResponse>builder()
                .code(200)
                .data(groupService.getGroupById(id))
                .build();
    }

    @GetMapping("/all")
    @Operation(summary = "Lấy tất cả nhóm", description = "Lấy danh sách tất cả các nhóm hiện có")
    public ApiResponse<List<GroupResponse>> getAllDSGroup() {
        return ApiResponse.<List<GroupResponse>>builder()
                .code(200)
                .data(groupService.getAllDSGroup())
                .build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lấy danh sách nhóm theo người dùng", description = "Lấy danh sách các nhóm mà người dùng đã tham gia dựa vào ID người dùng")
    public ApiResponse<List<GroupResponse>> getGroupsByUserId(@PathVariable("userId") Integer userId) {
        return ApiResponse.<List<GroupResponse>>builder()
                .code(200)
                .data(groupService.getGroupsByUserId(userId))
                .build();
    }

    @GetMapping("/{id}/baiviet")
    @Operation(summary = "Lấy danh sách bài viết theo nhóm", description = "Lấy danh sách tất cả các bài viết thuộc một nhóm dựa vào ID nhóm")
    public ApiResponse<List<BaiVietResponse>> getBaiVietByGroupId(@PathVariable("id") Integer id) {
        return ApiResponse.<List<BaiVietResponse>>builder()
                .code(200)
                .data(groupService.dsBaiVietTheoGroup(id))
                .build();
    }

}
