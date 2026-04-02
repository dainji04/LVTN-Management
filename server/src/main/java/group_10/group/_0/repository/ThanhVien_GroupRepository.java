package group_10.group._0.repository;


import group_10.group._0.entity.ThanhVienNhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanhVien_GroupRepository extends JpaRepository<ThanhVienNhom, Integer> {

    List<ThanhVienNhom> findByMaNhom_MaNhom(Integer idGroup);
}
