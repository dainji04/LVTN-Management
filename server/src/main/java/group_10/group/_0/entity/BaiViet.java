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
@Table(name = "BaiViet", indexes = {
        @Index(name = "idx_BaiViet_User", columnList = "maNguoiDung")
})
public class BaiViet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBaiViet", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiDung", nullable = false)
    private Users maNguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhom") // Cho phép null nếu là bài đăng cá nhân
    private Nhom maNhom;

    @Size(max = 20)
    @Column(name = "TrangThai", length = 20)
    private String trangThai; // PENDING, APPROVED, REJECTED

    @Size(max = 200)
    @Column(name = "NoiDung", length = 200)
    private String noiDung;

    @ColumnDefault("0")
    @Column(name = "DaSua")
    private Boolean daSua;

    @Size(max = 50)
    @Column(name = "QuyenRiengTu", length = 50)
    private String quyenRiengTu;

    @Size(max = 100)
    @Column(name = "ViTri", length = 100)
    private String viTri;

    @Size(max = 10)
    @Column(name = "MauNen", length = 10)
    private String mauNen;

    @ColumnDefault("0")
    @Column(name = "LuotThich")
    private Integer luotThich;

    @ColumnDefault("0")
    @Column(name = "LuotBinhLuan")
    private Integer luotBinhLuan;

    @ColumnDefault("0")
    @Column(name = "LuotChiaSe")
    private Integer luotChiaSe;

    @Column(name = "NgayTao")
    private Instant ngayTao;

    @Column(name = "NgayCapNhat")
    private Instant ngayCapNhat;

}