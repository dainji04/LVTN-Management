package group_10.group._0.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaoCaoResponse {
    Integer id;
    Integer nguoiBaoCaoId;
    String loaiDoiTuong;
    Integer idDoiTuong;
    String lyDo;
    String trangThai;
    Instant thoiGianTao;
}
