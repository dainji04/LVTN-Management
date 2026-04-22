package group_10.group._0.service;

import group_10.group._0.dto.request.TheoDoiRequest;
import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.LoiMoiResponse;
import group_10.group._0.entity.LoiMoiKetBan;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.LoiMoiKetBanMapper;
import group_10.group._0.repository.LoiMoiKetBanRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoiMoiKetBanService {

    final LoiMoiKetBanRepository loiMoiRepository;
    final UsersRepository usersRepository;
    final ThongBaoService thongBaoService;
    final QuanHeBanBeService quanHeBanBeService;
    final LoiMoiKetBanMapper mapper;
    final TheoDoiService theoDoiService;

    // 1. Gửi lời mời kết bạn
    public LoiMoiResponse guiLoiMoi(Integer nguoiGuiId, Integer nguoiNhanId) {
        if (quanHeBanBeService.areFriends(nguoiGuiId, nguoiNhanId))
            throw new AppExceptions(ErrorCode.FRIEND_ALREADY_EXISTED);

        if (loiMoiRepository.existsByMaNguoiGui_MaNguoiDungAndMaNguoiNhan_MaNguoiDungAndTrangThai(
                nguoiGuiId, nguoiNhanId, "DA_GUI"))
            throw new AppExceptions(ErrorCode.FRIEND_REQUEST_ALREADY_SENT);

        Users nguoiGui = usersRepository.findById(nguoiGuiId)
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        Users nguoiNhan = usersRepository.findById(nguoiNhanId)
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        LoiMoiKetBan loiMoi = LoiMoiKetBan.builder()
                .maNguoiGui(nguoiGui)
                .maNguoiNhan(nguoiNhan)
                .trangThai("DA_GUI")
                .build();

        LoiMoiKetBan saved = loiMoiRepository.save(loiMoi);

        // Gửi thông báo cho người nhận
        thongBaoService.taoMoiThongBao(ThongBaoRequest.builder()
                .maNguoiHanhDong(nguoiGuiId)
                .maNguoiNhan(nguoiNhanId)
                .loaiHanhDong("LOI_MOI_KET_BAN")
                .maDoiTuong(saved.getId())
                .loaiDoiTuong("LoiMoiKetBan")
                .build());
        theoDoiService.createTheoDoiKhongThongBao(
                TheoDoiRequest.builder()
                        .maNguoiTheoDoi(nguoiGuiId)
                        .maNguoiDuocTheoDoi(nguoiNhanId)
                        .build()
        );

        return mapper.toResponse(saved);
    }

    // 2. Xóa lời mời
    public void xoaLoiMoi(Integer loiMoiId) {
        if (!loiMoiRepository.existsById(loiMoiId))
            throw new AppExceptions(ErrorCode.FRIEND_REQUEST_NOT_EXISTED);
        loiMoiRepository.deleteById(loiMoiId);
    }

    // 3. Danh sách lời mời theo người nhận
    public List<LoiMoiResponse> danhSachLoiMoi(Integer nguoiNhanId) {
        return loiMoiRepository
                .findByMaNguoiNhan_MaNguoiDungAndTrangThai(nguoiNhanId, "DA_GUI")
                .stream()
                .map(mapper::toResponse) // dùng mapper
                .toList();
    }

    // 4. Update trạng thái lời mời
    public LoiMoiResponse capNhatTrangThai(Integer loiMoiId, String trangThai) {
        LoiMoiKetBan loiMoi = loiMoiRepository.findById(loiMoiId)
                .orElseThrow(() -> new AppExceptions(ErrorCode.FRIEND_REQUEST_NOT_EXISTED));
        loiMoi.setTrangThai(trangThai);
        return mapper.toResponse(loiMoiRepository.save(loiMoi));
    }

    // 5. Kiểm tra trạng thái có phải CHAP_NHAN không
    public boolean kiemTraTrangThai(Integer loiMoiId) {
        LoiMoiKetBan loiMoi = loiMoiRepository.findById(loiMoiId)
                .orElseThrow(() -> new AppExceptions(ErrorCode.FRIEND_REQUEST_NOT_EXISTED));
        return "CHAP_NHAN".equals(loiMoi.getTrangThai());
    }
}