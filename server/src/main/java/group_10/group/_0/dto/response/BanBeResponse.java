package group_10.group._0.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BanBeResponse {
    Integer maNguoiDung;
    String ho;
    String ten;
    String bietDanh;
    String anhDaiDien;
    String email;
    Instant hoatDongLanCuoi;
    Instant ngayKetBan;
}