package group_10.group._0.repository;

import group_10.group._0.entity.TheoDoi;
import group_10.group._0.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TheoDoiRepository extends JpaRepository<TheoDoi, Integer> {

    long countByMaNguoiDuocTheoDoi(Integer userId);

    long countByMaNguoiTheoDoi(Users maNguoiTheoDoi);

    @Query("SELECT t.maNguoiTheoDoi FROM TheoDoi t WHERE t.maNguoiDuocTheoDoi = :userId")
    List<Users> findFollowersByUserId(@Param("userId") Integer userId);


    @Query("SELECT u FROM Users u WHERE u.maNguoiDung IN (SELECT t.maNguoiDuocTheoDoi FROM TheoDoi t WHERE t.maNguoiTheoDoi = :user)")
    List<Users> findFollowingByUser(@Param("user") Users user);
}
