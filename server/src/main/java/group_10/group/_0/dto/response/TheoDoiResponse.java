package group_10.group._0.dto.response;

import group_10.group._0.entity.Users;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TheoDoiResponse {
    Integer id;

    Integer maNguoiTheoDoi;

    Integer maNguoiDuocTheoDoi;

    Instant ngayTheoDoi;
}
