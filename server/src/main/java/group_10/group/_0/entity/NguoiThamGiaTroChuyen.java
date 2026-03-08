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
public class NguoiThamGiaTroChuyen {
    @Id
    @Column(name = "Ma", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaCuocTroChuyen", nullable = false)
    private CuocTroChuyen maCuocTroChuyen;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiDung", nullable = false)
    private Users maNguoiDung;

    @Column(name = "VaiTro")
    private Integer vaiTro;

    @Size(max = 30)
    @Column(name = "BietDanh", length = 30)
    private String bietDanh;

    @ColumnDefault("b'0'")
    @Column(name = "TatThongBao")
    private Boolean tatThongBao;

    @ColumnDefault("b'0'")
    @Column(name = "TinNhanDaXem")
    private Boolean tinNhanDaXem;

    @Column(name = "LanCuoiXem")
    private Instant lanCuoiXem;

    @Column(name = "NgayThamGia")
    private Instant ngayThamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DuocThemBoi")
    private Users duocThemBoi;

}