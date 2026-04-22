package group_10.group._0.service;

import group_10.group._0.dto.response.*;
import group_10.group._0.entity.Nhom;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.repository.BaiVietRepository;
import group_10.group._0.repository.BaoCaoRepository;
import group_10.group._0.repository.GroupRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminDashboardService {

    UsersRepository usersRepository;
    BaiVietRepository baiVietRepository;
    GroupRepository groupRepository;
    BaoCaoRepository baoCaoRepository;
    AuthenticationService authenticationService;

    public DashboardOverviewResponse getOverviewStats(String token) {
        verifyAdmin(token);

        long totalUsers = usersRepository.count();
        long activeUsersToday = usersRepository.countActiveUsersSince(Instant.now().minus(24, ChronoUnit.HOURS));
        long totalPosts = baiVietRepository.count();
        long totalGroups = groupRepository.count();
        long totalPendingReports = baoCaoRepository.countByTrangThai("CHUA_XU_LY");

        return DashboardOverviewResponse.builder()
                .totalUsers(totalUsers)
                .activeUsersToday(activeUsersToday)
                .totalPosts(totalPosts)
                .totalGroups(totalGroups)
                .totalPendingReports(totalPendingReports)
                .build();
    }

    public List<ChartPointResponse> getPostCreationChart(String token) {
        verifyAdmin(token);

        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<Object[]> results = baiVietRepository.countPostsCreatedPerDaySince(sevenDaysAgo);

        List<ChartPointResponse> chartData = new ArrayList<>();
        for (Object[] row : results) {
            chartData.add(ChartPointResponse.builder()
                    .date(row[0].toString())
                    .count(((Number) row[1]).longValue())
                    .build());
        }
        return chartData;
    }

    public LeaderboardsResponse getLeaderboards(String token) {
        verifyAdmin(token);

        // Top 5 creators based on total likes received across all their posts
        List<Object[]> topUsersData = usersRepository.findTopUsersByEngagement(PageRequest.of(0, 5));
        List<UserRankResponse> topCreators = new ArrayList<>();
        for (Object[] row : topUsersData) {
            Users user = (Users) row[0];
            Long totalLikes = ((Number) row[1]).longValue();
            topCreators.add(UserRankResponse.builder()
                    .maNguoiDung(user.getMaNguoiDung())
                    .ho(user.getHo())
                    .ten(user.getTen())
                    .anhDaiDien(user.getAnhDaiDien())
                    .totalLikes(totalLikes)
                    .build());
        }

        // Top 5 groups based on member count
        List<Nhom> topGroupsData = groupRepository.findTopGroupsByMembers(PageRequest.of(0, 5));
        List<GroupRankResponse> topGroups = topGroupsData.stream().map(g -> GroupRankResponse.builder()
                .id(g.getId())
                .tenNhom(g.getTenNhom())
                .anhDaiDien(g.getAnhDaiDien())
                .soThanhVien(g.getSoThanhVien())
                .build()).toList();

        return LeaderboardsResponse.builder()
                .topCreators(topCreators)
                .topGroups(topGroups)
                .build();
    }

    private void verifyAdmin(String token) {
        try {
            authenticationService.getRole(token);
        } catch (Exception e) {
            log.error("Xác thực Admin thất bại: {}", e.getMessage());
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }
    }

    public List<UserRankResponse> getBannedUsers(String token) {
        verifyAdmin(token);
        return usersRepository.findBannedUsers().stream()
                .map(u -> UserRankResponse.builder()
                        .maNguoiDung(u.getMaNguoiDung())
                        .ho(u.getHo())
                        .ten(u.getTen())
                        .anhDaiDien(u.getAnhDaiDien())
                        .totalLikes(0L) // Không cần thiết cho danh sách bị khóa, filler
                        .build())
                .toList();
    }

    public List<GroupRankResponse> getBannedGroups(String token) {
        verifyAdmin(token);
        return groupRepository.findBannedGroups().stream()
                .map(g -> GroupRankResponse.builder()
                        .id(g.getId())
                        .tenNhom(g.getTenNhom())
                        .anhDaiDien(g.getAnhDaiDien())
                        .soThanhVien(g.getSoThanhVien())
                        .build())
                .toList();
    }

    public List<Map<String, Object>> getBannedPosts(String token) {
        verifyAdmin(token);
        return baiVietRepository.findBannedPosts().stream()
                .map(b -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", b.getId());
                    map.put("noiDung", b.getNoiDung() != null ? b.getNoiDung() : "");
                    map.put("ngayTao", b.getNgayTao() != null ? b.getNgayTao().toString() : "");
                    return map;
                })
                .toList();
    }
}
