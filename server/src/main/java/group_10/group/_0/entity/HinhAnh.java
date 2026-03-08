package group_10.group._0.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "HinhAnh", indexes = {
        @Index(name = "idx_HinhAnh_DoiTuong", columnList = "MaDoiTuong, LoaiDoiTuong")
})
public class HinhAnh {
    @Id
    @Column(name = "MaHinhAnh", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // thêm dòng này
    private Integer maHinhAnh;


    @NotNull
    @Column(name = "MaDoiTuong", nullable = false)
    private Integer maDoiTuong;

    @Size(max = 50)
    @NotNull
    @Column(name = "LoaiDoiTuong", nullable = false, length = 50)
    private String loaiDoiTuong;

    @Size(max = 500)
    @NotNull
    @Column(name = "DuongDan", nullable = false, length = 500)
    private String duongDan;

    @Column(name = "NgayTao")
    private Instant ngayTao;

    @Column(name = "NgayCapNhat")
    private Instant ngayCapNhat;

}