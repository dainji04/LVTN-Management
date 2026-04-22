package group_10.group._0.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoiYKetBanResponse {

    private Integer maNguoiDung;
    private String ho;
    private String ten;
    private String bietDanh;
    private String anhDaiDien;
    private String email;
}