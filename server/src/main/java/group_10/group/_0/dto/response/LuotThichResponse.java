package group_10.group._0.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LuotThichResponse {
    Integer id;
    Integer maNguoiDung;
    String hoTen;
    Integer maDoiTuong;
    String loaiDoiTuong;
    String camXuc;
    Instant ngayTao;
    String anhDaiDien;
}
