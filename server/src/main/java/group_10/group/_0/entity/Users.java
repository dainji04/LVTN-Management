package group_10.group._0.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNguoiDung", nullable = false)
    private Integer maNguoiDung;


    @Size(max = 50)
    @Column(name = "Ho", length = 50)
    private String ho;

    @Size(max = 10)
    @Column(name = "Ten", length = 10)
    private String ten;

    @Size(max = 15)
    @Column(name = "BietDanh", length = 15)
    private String bietDanh;

    @Size(max = 100)
    @Column(name = "AnhDaiDien", length = 100)
    private String anhDaiDien;

    @Size(max = 100)
    @Column(name = "AnhNen", length = 255)
    private String anhNen;

    @Column(name = "NgaySinh")
    private Instant ngaySinh;

    @Size(max = 100)
    @Column(name = "GioiThieu", length = 100)
    private String gioiThieu;

    @Size(max = 50)
    @Column(name = "NoiLamViec", length = 50)
    private String noiLamViec;

    @Size(max = 50)
    @Column(name = "NoiHocTap", length = 50)
    private String noiHocTap;

    @Size(max = 30)
    @Column(name = "Email", length = 30)
    private String email;

    @Size(max = 10)
    @Column(name = "SoDienThoai", length = 10)
    private String soDienThoai;

    @Size(max = 255)
    @Column(name = "MatKhau", length = 255)
    private String matKhau;

    @Column(name = "NgayTao")
    private Instant ngayTao;

    @Column(name = "NgayCapNhat")
    private Instant ngayCapNhat;

    @Column(name = "BiCam")
    private Boolean biCam;

    @Column(name = "HoatDongLanCuoi")
    private Instant hoatDongLanCuoi;

    @Column(name = "GioiTinh")
    String gioiTinh;

}