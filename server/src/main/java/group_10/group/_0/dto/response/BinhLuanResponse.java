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
public class BinhLuanResponse {
    private Integer id;
    private Integer maNguoiDung;
    private String hoTen;
    private String anhDaiDien;
    private Integer maBaiDang;
    private Integer maNhom;
    private String noiDung;
    private Integer maBinhLuanCha;
    private Boolean daChinhSua;
    private Instant ngayTao;
    private Instant ngayCapNhat;
}