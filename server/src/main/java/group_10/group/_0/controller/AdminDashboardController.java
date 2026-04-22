package group_10.group._0.controller;

import group_10.group._0.dto.response.ApiResponse;
import group_10.group._0.dto.response.ChartPointResponse;
import group_10.group._0.dto.response.DashboardOverviewResponse;
import group_10.group._0.dto.response.LeaderboardsResponse;
import group_10.group._0.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Admin Dashboard", description = "API thống kê dành cho quản trị viên")
public class AdminDashboardController {

    AdminDashboardService adminDashboardService;

    @GetMapping("/overview")
    @Operation(summary = "Lấy thống kê tổng quan", description = "Tổng số người dùng, bài viết, nhóm và các báo cáo chưa xử lý")
    public ApiResponse<DashboardOverviewResponse> getOverview(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ApiResponse.<DashboardOverviewResponse>builder()
                .code(200)
                .data(adminDashboardService.getOverviewStats(jwtToken))
                .build();
    }

    @GetMapping("/stats/posts")
    @Operation(summary = "Lấy thống kê bài viết 7 ngày gần nhất", description = "Trả về số lượng bài viết mới được tạo mỗi ngày trong tuần qua")
    public ApiResponse<List<ChartPointResponse>> getPostStatsChart(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ApiResponse.<List<ChartPointResponse>>builder()
                .code(200)
                .data(adminDashboardService.getPostCreationChart(jwtToken))
                .build();
    }

    @GetMapping("/leaderboards")
    @Operation(summary = "Lấy bảng xếp hạng Leaderboard", description = "Top 5 người dùng tích cực và Top 5 nhóm năng động nhất")
    public ApiResponse<LeaderboardsResponse> getLeaderboards(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ApiResponse.<LeaderboardsResponse>builder()
                .code(200)
                .data(adminDashboardService.getLeaderboards(jwtToken))
                .build();
    }

    @GetMapping("/banned-users")
    @Operation(summary = "Lấy danh sách người dùng bị cấm", description = "Dành riêng cho Admin xem danh sách tài khoản đang bị khóa")
    public ApiResponse<List<group_10.group._0.dto.response.UserRankResponse>> getBannedUsers(@RequestHeader("Authorization") String token) {
        return ApiResponse.<List<group_10.group._0.dto.response.UserRankResponse>>builder()
                .code(200)
                .data(adminDashboardService.getBannedUsers(token.replace("Bearer ", "")))
                .build();
    }

    @GetMapping("/banned-groups")
    @Operation(summary = "Lấy danh sách nhóm bị cấm", description = "Dành riêng cho Admin xem danh sách nhóm đang bị khóa")
    public ApiResponse<List<group_10.group._0.dto.response.GroupRankResponse>> getBannedGroups(@RequestHeader("Authorization") String token) {
        return ApiResponse.<List<group_10.group._0.dto.response.GroupRankResponse>>builder()
                .code(200)
                .data(adminDashboardService.getBannedGroups(token.replace("Bearer ", "")))
                .build();
    }

    @GetMapping("/banned-posts")
    @Operation(summary = "Lấy danh sách bài viết bị cấm", description = "Dành riêng cho Admin xem danh sách bài viết đang bị khóa")
    public ApiResponse<List<java.util.Map<String, Object>>> getBannedPosts(@RequestHeader("Authorization") String token) {
        return ApiResponse.<List<java.util.Map<String, Object>>>builder()
                .code(200)
                .data(adminDashboardService.getBannedPosts(token.replace("Bearer ", "")))
                .build();
    }
}
