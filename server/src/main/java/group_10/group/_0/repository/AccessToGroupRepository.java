package group_10.group._0.repository;

import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.YeuCauThamGiaNhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessToGroupRepository extends JpaRepository<YeuCauThamGiaNhom, Integer> {
}
