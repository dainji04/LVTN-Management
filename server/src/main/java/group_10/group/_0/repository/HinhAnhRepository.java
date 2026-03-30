package group_10.group._0.repository;

import group_10.group._0.entity.HinhAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhRepository extends JpaRepository<HinhAnh, Integer> {

    // Lấy tất cả ảnh theo đối tượng và loại
    List<HinhAnh> findByMaDoiTuongAndLoaiDoiTuong(Integer maDoiTuong, String loaiDoiTuong);

    // Xóa tất cả ảnh của 1 đối tượng
    void deleteByMaDoiTuongAndLoaiDoiTuong(Integer maDoiTuong, String loaiDoiTuong);
}