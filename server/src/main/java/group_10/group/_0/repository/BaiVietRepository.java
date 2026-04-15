package group_10.group._0.repository;

import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.HinhAnh;
import group_10.group._0.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaiVietRepository extends JpaRepository<BaiViet, Integer> {
    // Lấy tất cả bài viết của 1 user
    List<BaiViet> findByMaNguoiDung_MaNguoiDung(Integer maNguoiDung);

    Slice<BaiViet> findAllByOrderByNgayTaoDesc(Pageable pageable);

    Slice<BaiViet> findByMaNguoiDung_MaNguoiDungOrderByNgayTaoDesc(Integer maNguoiDung, Pageable pageable); // thêm dòng này

    List<BaiViet> findByMaNhom_Id(Integer maNhom);
}
