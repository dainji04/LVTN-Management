package group_10.group._0.repository;

import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.YeuCauThamGiaNhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessToGroupRepository extends JpaRepository<YeuCauThamGiaNhom, Integer> {
    List<YeuCauThamGiaNhom> findByMaNhom_IdAndTrangThai(Integer maNhom, String trangThai);
}
