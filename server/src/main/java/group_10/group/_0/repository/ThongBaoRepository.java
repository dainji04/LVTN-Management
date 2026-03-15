package group_10.group._0.repository;

import group_10.group._0.entity.ThongBao;
import group_10.group._0.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {
    List<ThongBao> findByMaNguoiNhan_IdOrderByNgayTaoDesc(Integer userId);
}
