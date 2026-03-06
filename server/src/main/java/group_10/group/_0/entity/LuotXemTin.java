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
public class LuotXemTin {
    @Id
    @Column(name = "MaLuot", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiXem", nullable = false)
    private Users maNguoiXem;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaTin", nullable = false)
    private Tin maTin;

    @Column(name = "ThoiGianXem")
    private Instant thoiGianXem;

    @Size(max = 20)
    @Column(name = "CamXuc", length = 20)
    private String camXuc;

}