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
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + request.getEmail());
        }

        Users user = mapper.toTaikhoan(request);
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
//        user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        user.setMatKhau(request.getMatKhau());
        user.setNgayTao(Instant.now());
        user.setNgayCapNhat(Instant.now());

        return mapper.toTaikhoanResponse(repository.save(user));
    }

    public UsersResponse updateUser(Integer id, UsersRequest request) {
        Users user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        mapper.updateTaikhoan(user, request); // mapper update trực tiếp vào entity
        user.setNgayCapNhat(Instant.now());

        return mapper.toTaikhoanResponse(repository.save(user));
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