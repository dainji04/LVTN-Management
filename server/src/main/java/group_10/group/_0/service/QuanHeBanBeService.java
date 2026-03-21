package group_10.group._0.service;

import group_10.group._0.dto.response.BanBeResponse;
import group_10.group._0.entity.QuanHeBanBe;
import group_10.group._0.entity.Users;
import group_10.group._0.repository.QuanHeBanBeRepository;
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

    public boolean areFriends(Integer id1, Integer id2) {
        return repository.areFriends(id1, id2);
    }

    public List<BanBeResponse> getFriends(Integer userId) {
        List<QuanHeBanBe> friends = repository.findFriends(userId);
        return friends.stream().map(q -> {
            Users friend = q.getMaNguoiDung1().getMaNguoiDung().equals(userId)
                    ? q.getMaNguoiDung2()
                    : q.getMaNguoiDung1();
            return BanBeResponse.builder()
                    .maNguoiDung(friend.getMaNguoiDung())
                    .ho(friend.getHo())
                    .ten(friend.getTen())
                    .bietDanh(friend.getBietDanh())
                    .anhDaiDien(friend.getAnhDaiDien())
                    .email(friend.getEmail())
                    .hoatDongLanCuoi(friend.getHoatDongLanCuoi())
                    .ngayKetBan(q.getNgayTao())
                    .build();
        }).toList();
    }

    @Transactional
    public void removeFriend(Integer id1, Integer id2) {
        repository.removeFriend(id1, id2);
    }

    public QuanHeBanBe addFriend(Integer id1, Integer id2) {
        if (areFriends(id1, id2)) throw new RuntimeException("Đã là bạn bè rồi!");
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
        return repository.save(quanHe);
    }
}