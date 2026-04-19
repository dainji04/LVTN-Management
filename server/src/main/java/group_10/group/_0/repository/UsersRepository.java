package group_10.group._0.repository;

import group_10.group._0.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);

    // Đếm số lượng người dùng có hoạt động kể từ mốc thời gian startTime (Dùng để tính Daily Active Users)
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(u) FROM Users u WHERE u.hoatDongLanCuoi >= :startTime")
    long countActiveUsersSince(@org.springframework.data.repository.query.Param("startTime") java.time.Instant startTime);

    // Lấy top người dùng có độ tương tác cao nhất bằng cách cộng gộp số lượt Thích từ tất cả bài viết của họ
    @org.springframework.data.jpa.repository.Query("SELECT u, SUM(b.luotThich) as totalLikes FROM Users u JOIN BaiViet b ON u.maNguoiDung = b.maNguoiDung GROUP BY u ORDER BY totalLikes DESC")
    java.util.List<Object[]> findTopUsersByEngagement(org.springframework.data.domain.Pageable pageable);
}
