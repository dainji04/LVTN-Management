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
public class CamXucTinNhan {
    @Id
    @Column(name = "Ma", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiDung", nullable = false)
    private Users maNguoiDung;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaTinNhan", nullable = false)
    private TinNhan maTinNhan;

    @Size(max = 50)
    @Column(name = "CamXuc", length = 50)
    private String camXuc;

    @Column(name = "NgayTao")
    private Instant ngayTao;

}