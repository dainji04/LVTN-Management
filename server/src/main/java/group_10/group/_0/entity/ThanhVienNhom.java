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
@Table(name = "ThanhVienNhom", indexes = {
        @Index(name = "idx_ThanhVien_Nhom", columnList = "MaNhom")
})
public class ThanhVienNhom {
    @Id
    @Column(name = "MaThanhVien", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiDung", nullable = false)
    private Users maNguoiDung;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNhom", nullable = false)
    private Nhom maNhom;

    @Size(max = 50)
    @Column(name = "VaiTro", length = 50)
    private String vaiTro;

    @Column(name = "NgayThamGia")
    private Instant ngayThamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DuocMoiBoi")
    private Users duocMoiBoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ChapNhanBoi")
    private Users chapNhanBoi;

}