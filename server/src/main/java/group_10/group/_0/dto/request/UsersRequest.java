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
public class UsersRequest {

    @NotBlank(message = "Họ không được để trống")
    @Size(max = 50, message = "Ho tối đa 50 ký tự")
    String ho;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 50, message = "Ten tối đa 10 ký tự")
    String ten;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Size(max = 30, message = "Email tối đa 30 ký tự")
    String email;

    @Size(max = 10, message = "Gioi tinh tối đa 10 ký tự")
    @NotBlank(message = "Giới tính không được để trống")
    String gioiTinh;

    @Size(min = 6, message = "Mat khau tối thiểu 6 ký tự")
    @Size(max = 255, message = "Mat khau tối đa 255 ký tự")
    @NotBlank(message = "Mật khẩu không được để trống")
    String matKhau;

    @Size(max = 15, message = "Biet danh tối đa 15 ký tự")
    String bietDanh;

    @Size(max = 100, message = "Anh dai dien tối đa 100 ký tự")
    String anhDaiDien;

    @Size(max = 255, message = "Anh nen tối đa 100 ký tự")
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


}