package group_10.group._0.service;

import group_10.group._0.dto.request.TheoDoiRequest;
import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.BanBeResponse;
import group_10.group._0.dto.response.GoiYKetBanResponse;
import group_10.group._0.dto.response.SliceResponse;
import group_10.group._0.entity.LoiMoiKetBan;
import group_10.group._0.entity.QuanHeBanBe;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.BanBeMapper;
import group_10.group._0.repository.LoiMoiKetBanRepository;
import group_10.group._0.repository.QuanHeBanBeRepository;
import group_10.group._0.repository.TheoDoiRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuanHeBanBeService {

    final QuanHeBanBeRepository quanHeBanBeRepository;
    final UsersRepository usersRepository;
    final ThongBaoService thongBaoService;
    final LoiMoiKetBanRepository loiMoiRepository;
    final TheoDoiService theoDoiService;
    final TheoDoiRepository theoDoiRepository;

    final BanBeMapper mapper; // inject mapper

    public boolean areFriends(Integer id1, Integer id2) {
        return quanHeBanBeRepository.areFriends(id1, id2);
    }


    @Transactional
    public void removeFriend(Integer id1, Integer id2) {
        quanHeBanBeRepository.removeFriend(id1, id2);
    }

    public QuanHeBanBe addFriend(Integer id1, Integer id2, Integer loiMoiId) {
        if (areFriends(id1, id2)) throw new AppExceptions(ErrorCode.FRIEND_ALREADY_EXISTED);

        // Kiểm tra trạng thái lời mời
        LoiMoiKetBan loiMoi = loiMoiRepository.findById(loiMoiId)
                .orElseThrow(() -> new AppExceptions(ErrorCode.FRIEND_REQUEST_NOT_EXISTED));
        if (!"CHAP_NHAN".equals(loiMoi.getTrangThai()))
            throw new AppExceptions(ErrorCode.FRIEND_REQUEST_NOT_ACCEPTED);

        Users user1 = usersRepository.findById(id1)
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        Users user2 = usersRepository.findById(id2)
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        QuanHeBanBe quanHe = QuanHeBanBe.builder()
                .maNguoiDung1(user1)
                .maNguoiDung2(user2)
                .ngayTao(Instant.now())
                .ngayCapNhat(Instant.now())
                .build();

        QuanHeBanBe saved = quanHeBanBeRepository.save(quanHe);

        // Gửi thông báo cho user2
        thongBaoService.taoMoiThongBao(ThongBaoRequest.builder()
                .maNguoiHanhDong(id1)
                .maNguoiNhan(id2)
                .loaiHanhDong("KET_BAN")
                .maDoiTuong(saved.getId())
                .loaiDoiTuong("QuanHeBanBe")
                .build());

        // Follow người kia (không gửi thông báo)
        if (!theoDoiRepository.existsByMaNguoiTheoDoi_MaNguoiDungAndMaNguoiDuocTheoDoi(id1, id2)) {
            theoDoiService.createTheoDoiKhongThongBao(
                    TheoDoiRequest.builder()
                            .maNguoiTheoDoi(id1)
                            .maNguoiDuocTheoDoi(id2)
                            .build()
            );
        }

        return saved;
    }


    // Lấy danh sách bạn bè của user
    public List<BanBeResponse> getFriends(Integer userId) {
        return quanHeBanBeRepository.findFriends(userId).stream().map(q -> {
            Users friend = q.getMaNguoiDung1().getMaNguoiDung().equals(userId)
                    ? q.getMaNguoiDung2()
                    : q.getMaNguoiDung1();
            BanBeResponse response = mapper.toBanBeResponse(friend);
            response.setNgayKetBan(q.getNgayTao()); // set thêm ngayKetBan
            return response;
        }).toList();
    }

    // Lấy thông tin bạn bè theo danh sách ID
    public List<BanBeResponse> getFriendsByIds(List<Integer> ids) {
        return usersRepository.findAllById(ids).stream()
                .map(mapper::toBanBeResponse)
                .toList();
    }

    public List<BanBeResponse> searchUsers(String query) {
        Pageable pageable = PageRequest.of(0, 20);
        return quanHeBanBeRepository.searchUsers(query, pageable).stream()
                .map(user -> BanBeResponse.builder()
                        .maNguoiDung(user.getMaNguoiDung())
                        .ho(user.getHo())
                        .ten(user.getTen())
                        .bietDanh(user.getBietDanh())
                        .anhDaiDien(user.getAnhDaiDien())
                        .email(user.getEmail())
                        .hoatDongLanCuoi(user.getHoatDongLanCuoi())
                        .ngayKetBan(null)
                        .build()
                )
                .toList();
    }


    public List<BanBeResponse> searchFriends(Integer userId, String query) {
        String keyword = query.toLowerCase();
        return quanHeBanBeRepository.findFriends(userId).stream().map(q ->
                        q.getMaNguoiDung1().getMaNguoiDung().equals(userId)
                                ? q.getMaNguoiDung2()
                                : q.getMaNguoiDung1()
                )
                .filter(friend ->
                        (friend.getHo() != null && friend.getHo().toLowerCase().contains(keyword)) ||
                                (friend.getTen() != null && friend.getTen().toLowerCase().contains(keyword)) ||
                                (friend.getBietDanh() != null && friend.getBietDanh().toLowerCase().contains(keyword)) ||
                                (friend.getEmail() != null && friend.getEmail().toLowerCase().contains(keyword))
                )
                .limit(20)
                .map(mapper::toBanBeResponse)
                .toList();
    }

    public SliceResponse<GoiYKetBanResponse> goiYKetBan(int page, int size) {
        Users currentUser = getCurrentUser(); //

        Pageable pageable = PageRequest.of(page, size);
        Slice<Users> slice = quanHeBanBeRepository.findNguoiChuaKetBan(
                currentUser.getMaNguoiDung(), pageable);

        List<GoiYKetBanResponse> content = slice.getContent().stream()
                .map(u -> GoiYKetBanResponse.builder()
                        .maNguoiDung(u.getMaNguoiDung())
                        .ho(u.getHo())
                        .ten(u.getTen())
                        .bietDanh(u.getBietDanh())
                        .anhDaiDien(u.getAnhDaiDien())
                        .email(u.getEmail())
                        .build())
                .toList();

        return SliceResponse.<GoiYKetBanResponse>builder()
                .content(content)
                .hasNext(slice.hasNext())
                .page(page)
                .size(size)
                .build();
    }

    private Users getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
    }

}