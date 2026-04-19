package group_10.group._0.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupRankResponse {
    Integer id;
    String tenNhom;
    String anhDaiDien;
    Integer soThanhVien;
}
