package group_10.group._0.repository;

import group_10.group._0.entity.Nhom;
import group_10.group._0.entity.TheoDoiNhom;
import group_10.group._0.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TheoDoiNhomRepository extends JpaRepository<TheoDoiNhom, Integer> {

    long countByMaNhom_Id(Integer maNhom);

    long countByMaNguoiTheoDoi(Users maNguoiTheoDoi);

    @Query("SELECT t.maNguoiTheoDoi FROM TheoDoiNhom t WHERE t.maNhom.id = :maNhom")
    List<Users> findFollowersByGroupId(@Param("maNhom") Integer maNhom);

    @Query("SELECT t.maNhom FROM TheoDoiNhom t WHERE t.maNguoiTheoDoi = :user")
    List<Nhom> findFollowingGroupsByUser(@Param("user") Users user);

    @Query("SELECT t.id FROM TheoDoiNhom t WHERE t.maNguoiTheoDoi.maNguoiDung = :followerId AND t.maNhom.id = :groupId")
    Integer findIdByFollowerAndGroup(
            @Param("followerId") Integer followerId,
            @Param("groupId") Integer groupId
    );

    boolean existsByMaNguoiTheoDoi_MaNguoiDungAndMaNhom_Id(
            Integer maNguoiTheoDoi, Integer maNhom);
}
