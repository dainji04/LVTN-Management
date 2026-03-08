package group_10.group._0.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Ho không được để trống")
    String ho;

    @NotBlank(message = "Ten không được để trống")
    String ten;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    String email;

    @NotBlank(message = "Mat khau không được để trống")
    @Size(min = 6, message = "Mat khau tối thiểu 6 ký tự")
    String matKhau;

    String bietDanh;
    String anhDaiDien;
    String anhNen;
    Instant ngaySinh;       // chỉ cần date, không cần time
    String gioiThieu;
    String noiLamViec;
    String noiHocTap;
    String soDienThoai;
}