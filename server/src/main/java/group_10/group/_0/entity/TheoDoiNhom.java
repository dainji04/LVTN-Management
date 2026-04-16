package group_10.group._0.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "TheoDoiNhom", uniqueConstraints = {
        @UniqueConstraint(name = "uq_TheoDoiNhom", columnNames = {"MaNguoiTheoDoi", "MaNhom"})
})
public class TheoDoiNhom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTheoDoiNhom", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiTheoDoi", nullable = false)
    private Users maNguoiTheoDoi;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNhom", nullable = false)
    private Nhom maNhom;

    @Column(name = "NgayTheoDoi")
    private Instant ngayTheoDoi;

}
