package group_10.group._0.service;

import group_10.group._0.dto.request.UsersRequest;
import group_10.group._0.dto.request.UsersUpdateRequest;
import group_10.group._0.dto.response.UsersResponse;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.UsersMapper;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;
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
        return repository.findAll()
                .stream()
                .map(mapper::toTaikhoanResponse)
                .toList();
    }

    public UsersResponse getUserById(Integer id) {
        Users user = repository.findById(id)
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        return mapper.toTaikhoanResponse(user);
        //        return toResponse(user); Cach ma mapper thuc su lam
    }
    // Map Entity -> Response DTO
//    private UsersResponse toResponse(Users user) {
//        return UsersResponse.builder()
//                .maNguoiDung(user.getMaNguoiDung())
//                .ho(user.getHo())
//                .ten(user.getTen())
//                .email(user.getEmail())
//                .ngayTao(user.getNgayTao())
//                .build();
//    }



    private String removeDiacritics(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D")
                .trim()
                .replaceAll("\\s+", "");
    }

    //Hàm lấy response của URL https://picsum.photos/820/312
    private String fetchPicsumUrl(int width, int height) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NEVER) // không follow, lấy Location header
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://picsum.photos/" + width + "/" + height))
                    .GET()
                    .build();

            HttpResponse<Void> response = client.send(httpRequest, HttpResponse.BodyHandlers.discarding());

            // Picsum trả về 302 với header Location chứa URL ảnh thật
            return response.headers()
                    .firstValue("Location")
                    .orElse("https://picsum.photos/" + width + "/" + height); // fallback

        } catch (Exception e) {
            return "https://picsum.photos/" + width + "/" + height; // fallback nếu lỗi
        }
    }

    public UsersResponse createUser(UsersRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new AppExceptions(ErrorCode.EMAIL_IS_EXISTED);
        }

        Users user = mapper.toTaikhoan(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        user.setNgayTao(Instant.now());
        user.setNgayCapNhat(Instant.now());

        // Tự động tạo avatar nếu không có
        if (request.getAnhDaiDien() == null || request.getAnhDaiDien().isBlank()) {
            String ten = removeDiacritics(request.getTen());
            String avatar = "https://api.dicebear.com/9.x/adventurer/svg?seed=" + ten;
            user.setAnhDaiDien(avatar);
        }

        if (request.getAnhNen() == null || request.getAnhNen().isBlank()) {
            user.setAnhNen(fetchPicsumUrl(820, 312));
        }
        return mapper.toTaikhoanResponse(repository.save(user));
    }

    public UsersResponse updateUser(Integer id, UsersUpdateRequest request) {
        Users user = repository.findById(id)
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        mapper.updateTaikhoan(user, request);

        // Chỉ update mật khẩu nếu có giá trị
        if (request.getMatKhau() != null && !request.getMatKhau().isBlank()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            user.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        }

        user.setNgayCapNhat(Instant.now());
        return mapper.toTaikhoanResponse(repository.save(user));
    }

    public void deleteUser(Integer id) {
        if (!repository.existsById(id)) {
            throw new AppExceptions(ErrorCode.USER_NOT_EXISTED);
        }
        repository.deleteById(id);
    }


    public UsersResponse getMyInfo(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        Users tk = repository.findByEmail(email).orElseThrow(
                () -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        return mapper.toTaikhoanResponse(tk);
    }

}