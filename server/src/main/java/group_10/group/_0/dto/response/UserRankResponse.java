package group_10.group._0.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRankResponse {
    Integer maNguoiDung;
    String ho;
    String ten;
    String anhDaiDien;
    Long totalLikes; 
}
