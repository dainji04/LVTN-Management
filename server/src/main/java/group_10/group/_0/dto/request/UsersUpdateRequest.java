package group_10.group._0.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersUpdateRequest {

    @Size(max = 50, message = "Ho tối đa 50 ký tự")
    String ho;

    @Size(max = 10, message = "Ten tối đa 10 ký tự")
    String ten;

    @Size(min = 6, max = 255, message = "Mat khau tu 6 den 255 ky tu")
    String matKhau;

    @Size(max = 15, message = "Biet danh tối đa 15 ký tự")
    String bietDanh;

    @Size(max = 100, message = "Anh dai dien tối đa 100 ký tự")
    String anhDaiDien;

    @Size(max = 100, message = "Anh nen tối đa 100 ký tự")
    String anhNen;

    Instant ngaySinh;

    @Size(max = 100, message = "Gioi thieu tối đa 100 ký tự")
    String gioiThieu;

    @Size(max = 50, message = "Noi lam viec tối đa 50 ký tự")
    String noiLamViec;

    @Size(max = 50, message = "Noi hoc tap tối đa 50 ký tự")
    String noiHocTap;

    @Size(max = 10, message = "So dien thoai tối đa 10 ký tự")
    @Pattern(regexp = "^[0-9]*$", message = "So dien thoai chi duoc chua so")
    String soDienThoai;

    @Size(max = 10, message = "Gioi tinh tối đa 10 ký tự")
    String gioiTinh;

}