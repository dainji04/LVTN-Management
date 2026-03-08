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
@Table(name = "TheoDoi", uniqueConstraints = {
        @UniqueConstraint(name = "uq_TheoDoi", columnNames = {"MaNguoiTheoDoi", "MaNguoiDuocTheoDoi"})
})
public class TheoDoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTheoDoi", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiTheoDoi", nullable = false)
    private Users maNguoiTheoDoi;

    @NotNull
    @Column(name = "MaNguoiDuocTheoDoi", nullable = false)
    private Integer maNguoiDuocTheoDoi;

    @Column(name = "NgayTheoDoi")
    private Instant ngayTheoDoi;

}