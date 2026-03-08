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
@Table(name = "TinNhan", indexes = {
        @Index(name = "idx_TinNhan_Convo", columnList = "MaCuocTroChuyen")
})
public class TinNhan {
    @Id
    @Column(name = "MaTinNhan", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaCuocTroChuyen", nullable = false)
    private CuocTroChuyen maCuocTroChuyen;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiGui", nullable = false)
    private Users maNguoiGui;

    @Size(max = 200)
    @Column(name = "NoiDung", length = 200)
    private String noiDung;

    @Size(max = 50)
    @Column(name = "LoaiTinNhan", length = 50)
    private String loaiTinNhan;

    @Size(max = 200)
    @Column(name = "DuongDanFile", length = 200)
    private String duongDanFile;

    @Size(max = 50)
    @Column(name = "TenFile", length = 50)
    private String tenFile;

    @Size(max = 20)
    @Column(name = "KichThuocFile", length = 20)
    private String kichThuocFile;

    @Column(name = "ThoiLuong")
    private Integer thoiLuong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TraLoiBoi")
    private TinNhan traLoiBoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ChuyenTiepTu")
    private TinNhan chuyenTiepTu;

    @ColumnDefault("b'0'")
    @Column(name = "DaXoa")
    private Boolean daXoa;

    @Column(name = "DaXoaLuc")
    private Instant daXoaLuc;

    @ColumnDefault("b'0'")
    @Column(name = "DaChinhSua")
    private Boolean daChinhSua;

    @Column(name = "SuaLuc")
    private Instant suaLuc;

    @Column(name = "NgayGuiTinNhan")
    private Instant ngayGuiTinNhan;

}