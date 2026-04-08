package group_10.group._0.repository;

import group_10.group._0.entity.LoiMoiKetBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoiMoiKetBanRepository extends JpaRepository<LoiMoiKetBan, Integer> {

    // Danh sách lời mời theo người nhận
    List<LoiMoiKetBan> findByMaNguoiNhan_MaNguoiDungAndTrangThai(
            Integer maNguoiNhan, String trangThai);

    // Kiểm tra đã gửi lời mời chưa
    boolean existsByMaNguoiGui_MaNguoiDungAndMaNguoiNhan_MaNguoiDungAndTrangThai(
            Integer maNguoiGui, Integer maNguoiNhan, String trangThai);
}