package group_10.group._0.repository;

import group_10.group._0.entity.Nhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Nhom,Integer> {

    @Query("SELECT COUNT(t) > 0 FROM ThanhVienNhom t WHERE t.maNguoiDung.maNguoiDung = :userId AND t.maNhom.id = :groupId")
    boolean existsByUserIdAndGroupId(@Param("userId") Integer userId, @Param("groupId") Integer groupId);


}
