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
public class GroupResponse {
    Integer id;

    Integer maNguoiTao;

    String tenNhom;

    String moTa;

    String anhBia;

    String anhDaiDien;

    Integer soThanhVien;

    String loaiNhom;

    Boolean canDuyetBaiDang;

    Instant ngayTao;
}
