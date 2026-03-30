package group_10.group._0.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoiMoiResponse {
    Integer id;
    Integer maNguoiGui;
    String hoTenNguoiGui;
    String anhDaiDienNguoiGui;
    Integer maNguoiNhan;
    String trangThai;
}
