package group_10.group._0.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LeaderboardsResponse {
    List<UserRankResponse> topCreators;
    List<GroupRankResponse> topGroups;
}
