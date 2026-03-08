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
public class Tin {
    @Id
    @Column(name = "MaTin", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaNguoiDang", nullable = false)
    private Users maNguoiDang;

    @Size(max = 50)
    @Column(name = "LoaiFile", length = 50)
    private String loaiFile;

    @Column(name = "DuongDanFile")
    private Integer duongDanFile;

    @Size(max = 200)
    @Column(name = "NoiDung", length = 200)
    private String noiDung;

    @Size(max = 50)
    @Column(name = "QuyenRiengTu", length = 50)
    private String quyenRiengTu;

    @Column(name = "ThoiGianHetHan")
    private Instant thoiGianHetHan;

    @Column(name = "NgayDangTin")
    private Instant ngayDangTin;

}