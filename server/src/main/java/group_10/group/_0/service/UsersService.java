package group_10.group._0.service;

import group_10.group._0.dto.UsersRequest;
import group_10.group._0.dto.UsersResponse;
import group_10.group._0.entity.Users;
import group_10.group._0.mapper.UsersMapper;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class UsersService {

    UsersRepository repository;
    UsersMapper mapper;

    public List<UsersResponse> getAllUsers() {
        List<Users> taikhoans = repository.findAll();
        List<UsersResponse> responses = new ArrayList<>();
        for (Users tk : taikhoans) {
            responses.add(mapper.toTaikhoanResponse(tk));
        }
        return responses;
    }

    public UsersResponse getUserById(Integer id) {
        Users user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return toResponse(user);
    }

    public UsersResponse createUser(UsersRequest request) {
        Users user = Users.builder()
                .ho(request.getHo())
                .ten(request.getTen())
                .email(request.getEmail())
                .matKhau(request.getMatKhau()) // nên hash password ở đây
                .ngayTao(Instant.now())
                .ngayCapNhat(Instant.now())
                .build();
        return toResponse(repository.save(user));
    }

    public UsersResponse updateUser(Integer id, UsersRequest request) {
        Users user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setHo(request.getHo());
        user.setTen(request.getTen());
        user.setEmail(request.getEmail());
        user.setNgayCapNhat(Instant.now());
        return toResponse(repository.save(user));
    }

    public void deleteUser(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        repository.deleteById(id);
    }

    // Map Entity -> Response DTO
    private UsersResponse toResponse(Users user) {
        return UsersResponse.builder()
                .maNguoiDung(user.getId())
                .ho(user.getHo())
                .ten(user.getTen())
                .email(user.getEmail())
                .ngayTao(user.getNgayTao())
                .build();
    }
}