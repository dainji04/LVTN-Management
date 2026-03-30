package group_10.group._0.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BaiVietRequest {

    @NotNull(message = "MaNguoiDung không được để trống")
    Integer maNguoiDung;

    @NotBlank(message = "NoiDung không được để trống")
    String noiDung;

    String quyenRiengTu;  // PUBLIC, FRIENDS, PRIVATE
    String viTri;
    String mauNen;

    List<Integer> danhSachCongTacVien;
    List<String> danhSachAnh;
}
