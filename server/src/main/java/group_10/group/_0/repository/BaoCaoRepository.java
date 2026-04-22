package group_10.group._0.repository;

import group_10.group._0.entity.BaoCao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaoCaoRepository extends JpaRepository<BaoCao, Integer> {
    
    // Tìm các báo cáo theo trạng thái (CHUA_XU_LY, DA_XU_LY, BO_QUA)
    List<BaoCao> findByTrangThai(String trangThai);
    
    // Tìm báo cáo dựa trên đối tượng cụ thể (VD: tìm tất cả report của bài viết A)
    List<BaoCao> findByLoaiDoiTuongAndIdDoiTuong(String loaiDoiTuong, Integer idDoiTuong);

    // Kiểm tra xem User A đã báo cáo đối tượng B chưa để tránh báo cáo spam trùng lặp
    boolean existsByNguoiBaoCao_MaNguoiDungAndLoaiDoiTuongAndIdDoiTuong(Integer maNguoiDung, String loaiDoiTuong, Integer idDoiTuong);

    // Đếm số lượng báo cáo theo trạng thái
    long countByTrangThai(String trangThai);
}
