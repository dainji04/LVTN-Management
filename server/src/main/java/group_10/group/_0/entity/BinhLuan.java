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
@Table(name = "BinhLuan", indexes = {
        @Index(name = "idx_BinhLuan_BaiDang", columnList = "MaBaiDang")
})
public class BinhLuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBinhLuan", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiDung", nullable = false)
    private Users maNguoiDung;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaBaiDang", nullable = false)
    private BaiViet maBaiDang;

    @Column(name = "MaNhom")
    private Integer maNhom;

    @Size(max = 200)
    @Column(name = "NoiDung", length = 200)
    private String noiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaBinhLuanCha")
    private BinhLuan maBinhLuanCha;

    @ColumnDefault("b'0'")
    @Column(name = "DaChinhSua")
    private Boolean daChinhSua;

    @Column(name = "NgayChinhSua")
    private Instant ngayChinhSua;

    @Column(name = "NgayTao")
    private Instant ngayTao;

    @Column(name = "NgayCapNhat")
    private Instant ngayCapNhat;

}