package group_10.group._0.repository;

import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.LuotThich;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LuotThichRepository extends JpaRepository<LuotThich, Integer> {

    // Đếm lượt thích theo đối tượng
    long countByMaDoiTuongAndLoaiDoiTuong(Integer maDoiTuong, String loaiDoiTuong);

    // Kiểm tra user đã thích chưa
    boolean existsByMaNguoiDung_MaNguoiDungAndMaDoiTuongAndLoaiDoiTuong(
            Integer maNguoiDung, Integer maDoiTuong, String loaiDoiTuong);

    // Lấy lượt thích của user với đối tượng
    Optional<LuotThich> findByMaNguoiDung_MaNguoiDungAndMaDoiTuongAndLoaiDoiTuong(
            Integer maNguoiDung, Integer maDoiTuong, String loaiDoiTuong);

    // Lấy danh sách người thích
    List<LuotThich> findByMaDoiTuongAndLoaiDoiTuong(Integer maDoiTuong, String loaiDoiTuong);

    List<LuotThich> findByMaNguoiDung_MaNguoiDungAndMaDoiTuongInAndLoaiDoiTuong(
            Integer maNguoiDung, List<Integer> maDoiTuong, String loaiDoiTuong);
}