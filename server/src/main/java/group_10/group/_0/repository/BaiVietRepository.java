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

    // Gom nhóm số lượng bài viết sinh ra theo từng ngày một để vẽ biểu đồ (Chart) 
    @org.springframework.data.jpa.repository.Query("SELECT FUNCTION('DATE', b.ngayTao) as date, COUNT(b) as count FROM BaiViet b WHERE b.ngayTao >= :startDate GROUP BY FUNCTION('DATE', b.ngayTao) ORDER BY date ASC")
    List<Object[]> countPostsCreatedPerDaySince(@org.springframework.data.repository.query.Param("startDate") java.time.Instant startDate);

    // Lấy Top bài viết Viral (Trending) nhất dựa vào việc cộng gộp số Like + số Bình luận + số Chia sẻ
    @org.springframework.data.jpa.repository.Query("SELECT b FROM BaiViet b ORDER BY (COALESCE(b.luotThich, 0) + COALESCE(b.luotBinhLuan, 0) + COALESCE(b.luotChiaSe, 0)) DESC")
    List<BaiViet> findTopTrendingPosts(Pageable pageable);
}
