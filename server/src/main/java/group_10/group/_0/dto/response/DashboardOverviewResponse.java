package group_10.group._0.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardOverviewResponse {
    long totalUsers;
    long activeUsersToday;
    long totalPosts;
    long totalGroups;
    long totalPendingReports;
}
