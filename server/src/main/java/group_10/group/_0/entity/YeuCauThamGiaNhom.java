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
public class YeuCauThamGiaNhom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaYeuCau", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNhom", nullable = false)
    private Nhom maNhom;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiDung", nullable = false)
    private Users maNguoiDung;

    @Size(max = 50)
    @Column(name = "TrangThai", length = 50)
    private String trangThai;

    @Column(name = "YeuCauLuc")
    private Instant yeuCauLuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "XuLyBoi")
    private Users xuLyBoi;

    @Column(name = "XuLyLuc")
    private Instant xuLyLuc;

}