package group_10.group._0.dto.response;

import group_10.group._0.entity.Users;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThongBaoResponse {

    private Integer maNguoiHanhDong;

    private Integer maNguoiNhan;

    private String loaiHanhDong;

    private Integer maDoiTuong;

    private String loaiDoiTuong;

    private Boolean daDoc;

    private Instant ngayTao;

}
