package group_10.group._0.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersResponse {

    Integer maNguoiDung;
    String ho;
    String ten;
    String bietDanh;
    String email;
    String anhDaiDien;
    String anhNen;
    Instant ngaySinh;
    String gioiThieu;
    String noiLamViec;
    String noiHocTap;
    String soDienThoai;
    Instant ngayTao;
    Instant ngayCapNhat;
}