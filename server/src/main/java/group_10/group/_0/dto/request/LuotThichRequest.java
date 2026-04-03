package group_10.group._0.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LuotThichRequest {
    Integer maNguoiDung;
    Integer maDoiTuong;
    String loaiDoiTuong; // BaiViet, BinhLuan
    String camXuc;      // LIKE, LOVE, HAHA, WOW, SAD, ANGRY
}
