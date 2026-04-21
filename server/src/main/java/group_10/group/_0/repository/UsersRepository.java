package group_10.group._0.repository;

import group_10.group._0.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);


    @Query("SELECT u FROM Users u WHERE u.biCam = false OR u.biCam IS NULL")
    List<Users> findAllActiveUsers();

    // Lấy mọi User sạch cho các API hiển thị thông thường
    List<Users> findByBiCamFalse();

    // Lấy danh sách bị cấm cho Admin Dashboard
    @Query("SELECT u FROM Users u WHERE u.biCam = true")
    List<Users> findBannedUsers();

    // Đếm số lượng người dùng có hoạt động kể từ mốc thời gian startTime (Dùng để
    // tính Daily Active Users)
    @Query("SELECT COUNT(u) FROM Users u WHERE u.hoatDongLanCuoi >= :startTime")
    long countActiveUsersSince(@Param("startTime") java.time.Instant startTime);

    // Lấy top người dùng có độ tương tác cao nhất bằng cách cộng gộp số lượt Thích
    // từ tất cả bài viết của họ
    @Query("SELECT b.maNguoiDung, SUM(b.luotThich) FROM BaiViet b GROUP BY b.maNguoiDung ORDER BY SUM(b.luotThich) DESC")
    List<Object[]> findTopUsersByEngagement(Pageable pageable);
}
