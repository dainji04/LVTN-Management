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
@Table(name = "LuotThich", indexes = {
        @Index(name = "idx_LuotThich_DoiTuong", columnList = "MaDoiTuong, LoaiDoiTuong")
})
public class LuotThich {
    @Id
    @Column(name = "MaLuotThich", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiDung", nullable = false)
    private Users maNguoiDung;

    @NotNull
    @Column(name = "MaDoiTuong", nullable = false)
    private Integer maDoiTuong;

    @Size(max = 20)
    @Column(name = "LoaiDoiTuong", length = 20)
    private String loaiDoiTuong;

    @Size(max = 50)
    @Column(name = "CamXuc", length = 50)
    private String camXuc;

    @Column(name = "NgayTao")
    private Instant ngayTao;

    @Column(name = "NgayCapNhat")
    private Instant ngayCapNhat;

}