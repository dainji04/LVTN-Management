package group_10.group._0.controller;

import group_10.group._0.dto.request.AccessToGroupRequest;
import group_10.group._0.dto.request.AccessToGroupUpdateRequest;
import group_10.group._0.dto.response.AccessToGroupResponse;
import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.service.AccessToGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/access-to-group")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Yêu cầu Join Nhóm", description = "API quản lý yêu cầu tham gia nhóm")
public class AccessToGroupController {

    AccessToGroupService accessToGroupService;

    @PostMapping
    @Operation(summary = "Tạo yêu cầu tham gia", description = "Người dùng gửi yêu cầu tham gia vào nhóm")
    public ApiResponse<AccessToGroupResponse> createRequestToGroup(@RequestBody AccessToGroupRequest request) {
        return ApiResponse.<AccessToGroupResponse>builder()
                .code(200)
                .data(accessToGroupService.createRequestToGroup(request))
                .build();
    }

    @GetMapping("/group/{idGroup}")
    @Operation(summary = "Danh sách yêu cầu chờ xử lý", description = "Lấy danh sách yêu cầu đang chờ quản trị viên duyệt")
    public ApiResponse<List<AccessToGroupResponse>> danhSachYeuCauChuaXuLy(@PathVariable Integer idGroup) {
        return ApiResponse.<List<AccessToGroupResponse>>builder()
                .code(200)
                .data(accessToGroupService.DSYeuCauChuaXuLy(idGroup))
                .build();
    }

    @DeleteMapping("/{idYeuCau}")
    @Operation(summary = "Người dùng hủy yêu cầu", description = "Hủy yêu cầu tham gia trước khi được duyệt")
    public ApiResponse<Void> huyYeuCau(@PathVariable Integer idYeuCau) {
        accessToGroupService.huyYeuCau(idYeuCau);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Hủy yêu cầu tham gia nhóm thành công")
                .build();
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Chấp nhận yêu cầu", description = "Quản trị viên chấp nhận yêu cầu tham gia của người dùng")
    public ApiResponse<Void> acceptRequest(@PathVariable Integer id, @RequestBody AccessToGroupUpdateRequest request, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        accessToGroupService.accept(id, request, jwtToken);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã chấp nhận yêu cầu tham gia")
                .build();
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Từ chối yêu cầu", description = "Quản trị viên từ chối yêu cầu tham gia của người dùng")
    public ApiResponse<Void> rejectRequest(@PathVariable Integer id, @RequestBody AccessToGroupUpdateRequest request, @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        accessToGroupService.reject(id, request, jwtToken);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Đã từ chối yêu cầu tham gia")
                .build();
    }
}
