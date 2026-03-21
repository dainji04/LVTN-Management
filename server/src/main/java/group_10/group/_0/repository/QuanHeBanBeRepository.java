package group_10.group._0.repository;

import group_10.group._0.entity.QuanHeBanBe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuanHeBanBeRepository extends JpaRepository<QuanHeBanBe, Integer> {

    // Kiểm tra 2 user có phải bạn bè không
    @Query("SELECT COUNT(q) > 0 FROM QuanHeBanBe q WHERE " +
            "(q.maNguoiDung1.maNguoiDung = :id1 AND q.maNguoiDung2.maNguoiDung = :id2) OR " +
            "(q.maNguoiDung1.maNguoiDung = :id2 AND q.maNguoiDung2.maNguoiDung = :id1)")
    boolean areFriends(@Param("id1") Integer id1, @Param("id2") Integer id2);

    // Lấy danh sách bạn bè
    @Query("SELECT q FROM QuanHeBanBe q " +
            "LEFT JOIN FETCH q.maNguoiDung1 " +
            "LEFT JOIN FETCH q.maNguoiDung2 " +
            "WHERE q.maNguoiDung1.maNguoiDung = :userId OR q.maNguoiDung2.maNguoiDung = :userId " +
            "ORDER BY q.ngayTao DESC")
    List<QuanHeBanBe> findFriends(@Param("userId") Integer userId);

    // Xóa bạn bè
    @Query("DELETE FROM QuanHeBanBe q WHERE " +
            "(q.maNguoiDung1.maNguoiDung = :id1 AND q.maNguoiDung2.maNguoiDung = :id2) OR " +
            "(q.maNguoiDung1.maNguoiDung = :id2 AND q.maNguoiDung2.maNguoiDung = :id1)")
    void removeFriend(@Param("id1") Integer id1, @Param("id2") Integer id2);
}