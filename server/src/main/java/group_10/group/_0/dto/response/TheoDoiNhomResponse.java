package group_10.group._0.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TheoDoiNhomResponse {
    Integer id;

    Integer maNguoiTheoDoi;

    Integer maNhom;

    Instant ngayTheoDoi;
}
