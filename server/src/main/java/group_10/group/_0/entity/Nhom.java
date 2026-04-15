package group_10.group._0.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Nhom {
    @Id
    @Column(name = "MaNhom", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiTao", nullable = false)
    private Users maNguoiTao;

    @Size(max = 50)
    @Column(name = "TenNhom", length = 50)
    private String tenNhom;

    @Size(max = 50)
    @Column(name = "MoTa", length = 50)
    private String moTa;

    @Size(max = 50)
    @Column(name = "AnhBia", length = 50)
    private String anhBia;

    @Size(max = 50)
    @Column(name = "AnhDaiDien", length = 50)
    private String anhDaiDien;

    @ColumnDefault("0")
    @Column(name = "SoThanhVien")
    private Integer soThanhVien;

    @Size(max = 50)
    @Column(name = "LoaiNhom", length = 50)
    private String loaiNhom;

    @ColumnDefault("b'0'")
    @Column(name = "CanDuyetBaiDang")
    private Boolean canDuyetBaiDang;

    @Column(name = "NgayTao")
    private Instant ngayTao;

    @Column(name = "NgayCapNhat")
    private Instant ngayCapNhat;

}