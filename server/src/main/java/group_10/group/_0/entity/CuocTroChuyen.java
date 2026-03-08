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
public class CuocTroChuyen {
    @Id
    @Column(name = "MaCuocTroChuyen", nullable = false)
    private Integer id;

    @Size(max = 100)
    @Column(name = "Ten", length = 100)
    private String ten;

    @Size(max = 100)
    @Column(name = "AnhDaiDien", length = 100)
    private String anhDaiDien;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiTao", nullable = false)
    private Users maNguoiTao;

    @ColumnDefault("0")
    @Column(name = "SoLuongThanhVien")
    private Integer soLuongThanhVien;

    @ColumnDefault("0")
    @Column(name = "SoLuongTinNhan")
    private Integer soLuongTinNhan;

    @Column(name = "NgayTao")
    private Instant ngayTao;

    @Column(name = "NgayCapNhat")
    private Instant ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTinNhanCuoi")
    private TinNhan maTinNhanCuoi;

}