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
public class ThanhVien_GroupResponse {

    Integer id;

    Integer maNguoiDung;

    Integer maNhom;

    String vaiTro;

    Instant ngayThamGia;

    Integer duocMoiBoi;

    Integer chapNhanBoi;
}
