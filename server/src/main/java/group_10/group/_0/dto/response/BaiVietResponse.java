package group_10.group._0.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaiVietResponse {

    Integer maBaiViet;
    Integer maNguoiDung;  // chỉ trả ID, không trả cả object Users
    String hoTen;         // ho + ten của người đăng
    String noiDung;
    Boolean daSua;
    String quyenRiengTu;
    String viTri;
    String mauNen;
    Integer luotThich;
    Integer luotBinhLuan;
    Integer luotChiaSe;
    Instant ngayTao;
    Instant ngayCapNhat;

    String anhDaiDienNguoiDang;
    List<String> danhSachAnh;
}