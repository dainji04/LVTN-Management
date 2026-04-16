package group_10.group._0.service;

import group_10.group._0.dto.request.LuotThichRequest;
import group_10.group._0.dto.response.LuotThichResponse;
import group_10.group._0.entity.LuotThich;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.LuotThichMapper;
import group_10.group._0.repository.BaiVietRepository;
import group_10.group._0.repository.LuotThichRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LuotThichService {

    final LuotThichRepository luotThichRepository;
    final UsersRepository usersRepository;
    final BaiVietRepository baiVietRepository;
    private final LuotThichMapper luotThichMapper;

    // Toggle like (thích/bỏ thích)
    public boolean toggleLike(LuotThichRequest request) {
        Optional<LuotThich> existing = luotThichRepository
                .findByMaNguoiDung_MaNguoiDungAndMaDoiTuongAndLoaiDoiTuong(
                        request.getMaNguoiDung(),
                        request.getMaDoiTuong(),
                        request.getLoaiDoiTuong());

        if (existing.isPresent()) {
            // Bỏ thích
            luotThichRepository.delete(existing.get());
            capNhatSoLuotThich(request.getMaDoiTuong(), request.getLoaiDoiTuong(), -1);
            return false; // đã bỏ thích
        } else {
            // Thích
            Users user = usersRepository.findById(request.getMaNguoiDung())
                    .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));

            LuotThich luotThich = luotThichMapper.toLuotThich(request);
            luotThich.setMaNguoiDung(user); // Gán object user đã tìm thấy

            luotThichRepository.save(luotThich);
            capNhatSoLuotThich(request.getMaDoiTuong(), request.getLoaiDoiTuong(), 1);
            return true; // đã thích
        }
    }

    // Cập nhật số lượt thích trong bảng BaiViet
    private void capNhatSoLuotThich(Integer maDoiTuong, String loaiDoiTuong, int delta) {
        if ("BaiViet".equals(loaiDoiTuong)) {
            baiVietRepository.findById(maDoiTuong).ifPresent(baiViet -> {
                baiViet.setLuotThich(Math.max(0, (baiViet.getLuotThich() == null ? 0 : baiViet.getLuotThich()) + delta));
                baiVietRepository.save(baiViet);
            });
        }
    }

    // Kiểm tra user đã thích chưa
    public boolean daThich(Integer maNguoiDung, Integer maDoiTuong, String loaiDoiTuong) {
        return luotThichRepository.existsByMaNguoiDung_MaNguoiDungAndMaDoiTuongAndLoaiDoiTuong(
                maNguoiDung, maDoiTuong, loaiDoiTuong);
    }

    // Danh sách người thích
    public List<LuotThichResponse> danhSachLuotThich(Integer maDoiTuong, String loaiDoiTuong) {
        return luotThichRepository.findByMaDoiTuongAndLoaiDoiTuong(maDoiTuong, loaiDoiTuong)
                .stream()
                .map(luotThichMapper::toLuotThichResponse)
                .toList();
    }
}