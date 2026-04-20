package group_10.group._0.repository;

import group_10.group._0.entity.Nhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Nhom, Integer> {

       @Query("SELECT COUNT(t) > 0 FROM ThanhVienNhom t WHERE t.maNguoiDung.maNguoiDung = :userId AND t.maNhom.id = :groupId")
       boolean existsByUserIdAndGroupId(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

       @Query("SELECT n FROM Nhom n WHERE n.id = :groupId")
       Nhom thongtincuaGroupById(@Param("groupId") Integer groupId);

       @Query("SELECT n FROM Nhom n WHERE n.tenNhom LIKE CONCAT('%', :keyword, '%')")
       List<Nhom> searchByGroupName(@Param("keyword") String keyword);

       @Query("SELECT n FROM Nhom n")
       List<Nhom> findAllGroups();

       // Lấy danh sách nhóm theo id người dùng đã tham gia
       @Query("SELECT t.maNhom FROM ThanhVienNhom t WHERE t.maNguoiDung.maNguoiDung = :userId")
       List<Nhom> findGroupsByUserId(@Param("userId") Integer userId);

       // Lấy danh sách Top Nhóm Năng Động (Nhiều thành viên nhất) làm mảng xếp hạng
       // Leaderboard
       @Query("SELECT n FROM Nhom n ORDER BY n.soThanhVien DESC")
       List<Nhom> findTopGroupsByMembers(Pageable pageable);

       // Dành cho dashboard quản lý nhóm vi phạm của Admin
       @Query("SELECT n FROM Nhom n WHERE n.biCam = true")
       List<Nhom> findBannedGroups();
}
