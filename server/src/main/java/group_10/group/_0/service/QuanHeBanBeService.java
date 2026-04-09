package group_10.group._0.service;

import group_10.group._0.dto.request.TheoDoiRequest;
import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.BanBeResponse;
import group_10.group._0.entity.LoiMoiKetBan;
import group_10.group._0.entity.QuanHeBanBe;
import group_10.group._0.entity.Users;
import group_10.group._0.mapper.BanBeMapper;
import group_10.group._0.repository.LoiMoiKetBanRepository;
import group_10.group._0.repository.QuanHeBanBeRepository;
import group_10.group._0.repository.TheoDoiRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuanHeBanBeService {

    final QuanHeBanBeRepository repository;
    final UsersRepository usersRepository;
    final ThongBaoService thongBaoService;
    final LoiMoiKetBanRepository loiMoiRepository;
    final TheoDoiService theoDoiService;
    final TheoDoiRepository theoDoiRepository;

    final BanBeMapper mapper; // inject mapper

    public boolean areFriends(Integer id1, Integer id2) {
        return repository.areFriends(id1, id2);
    }


    @Transactional
    public void removeFriend(Integer id1, Integer id2) {
        repository.removeFriend(id1, id2);
    }

    public QuanHeBanBe addFriend(Integer id1, Integer id2, Integer loiMoiId) {
        if (areFriends(id1, id2)) throw new RuntimeException("Đã là bạn bè rồi!");

        // Kiểm tra trạng thái lời mời
        LoiMoiKetBan loiMoi = loiMoiRepository.findById(loiMoiId)
                .orElseThrow(() -> new RuntimeException("Lời mời không tồn tại!"));
        if (!"CHAP_NHAN".equals(loiMoi.getTrangThai()))
            throw new RuntimeException("Lời mời chưa được chấp nhận!");

        Users user1 = usersRepository.findById(id1)
                .orElseThrow(() -> new RuntimeException("User not found: " + id1));
        Users user2 = usersRepository.findById(id2)
                .orElseThrow(() -> new RuntimeException("User not found: " + id2));

        QuanHeBanBe quanHe = QuanHeBanBe.builder()
                .maNguoiDung1(user1)
                .maNguoiDung2(user2)
                .ngayTao(Instant.now())
                .ngayCapNhat(Instant.now())
                .build();

        QuanHeBanBe saved = repository.save(quanHe);

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
        return repository.findFriends(userId).stream().map(q -> {
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

    public List<BanBeResponse> searchFriends(Integer userId, String query) {
        String keyword = query.toLowerCase();
        return repository.findFriends(userId).stream().map(q ->
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

}