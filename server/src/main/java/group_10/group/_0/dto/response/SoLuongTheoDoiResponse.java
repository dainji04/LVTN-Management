package group_10.group._0.dto.response;


import group_10.group._0.entity.Users;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SoLuongTheoDoiResponse {
    long tongNguoiTheoDoi;
    long tongDangTheoDoi;
    List<Users> danhSachNguoiTheoDoi;
    List<Users> danhSachDangTheoDoi;
}
