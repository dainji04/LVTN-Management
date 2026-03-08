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
@Table(name = "ThongBao", indexes = {
        @Index(name = "idx_ThongBao_NguoiNhan", columnList = "MaNguoiNhan")
})
public class ThongBao {
    @Id
    @Column(name = "MaThongBao", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiHanhDong", nullable = false)
    private Users maNguoiHanhDong;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiNhan", nullable = false)
    private Users maNguoiNhan;

    @Size(max = 50)
    @Column(name = "LoaiHanhDong", length = 50)
    private String loaiHanhDong;

    @Column(name = "MaDoiTuong")
    private Integer maDoiTuong;

    @Size(max = 50)
    @Column(name = "LoaiDoiTuong", length = 50)
    private String loaiDoiTuong;

    @ColumnDefault("b'0'")
    @Column(name = "DaDoc")
    private Boolean daDoc;

    @Column(name = "NgayTao")
    private Instant ngayTao;

}