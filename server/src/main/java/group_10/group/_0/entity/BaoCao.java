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
@Table(name = "BaoCao")
public class BaoCao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nguoi_bao_cao_id", nullable = false)
    Users nguoiBaoCao;

    @NotNull
    @Column(name = "loai_doi_tuong", nullable = false, length = 50)
    String loaiDoiTuong; // VD: "BAI_VIET", "NHOM", "NGUOI_DUNG"

    @NotNull
    @Column(name = "id_doi_tuong", nullable = false)
    Integer idDoiTuong;

    @NotNull
    @Column(name = "ly_do", nullable = false, columnDefinition = "TEXT")
    String lyDo;

    @Column(name = "trang_thai", length = 20)
    String trangThai; // VD: "CHUA_XU_LY", "DA_XU_LY", "BO_QUA"

    @Column(name = "thoi_gian_tao")
    Instant thoiGianTao;
}
