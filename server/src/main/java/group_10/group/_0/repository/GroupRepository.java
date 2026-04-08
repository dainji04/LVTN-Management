package group_10.group._0.repository;

import group_10.group._0.entity.Nhom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Nhom,Integer> {
}
