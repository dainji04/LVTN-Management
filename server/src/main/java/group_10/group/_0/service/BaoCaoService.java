package group_10.group._0.service;

import com.nimbusds.jose.JOSEException;
import group_10.group._0.dto.request.BaoCaoRequest;
import group_10.group._0.dto.request.BaoCaoUpdateRequest;
import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.BaoCaoResponse;
import group_10.group._0.entity.BaoCao;
import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.Nhom;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.BaoCaoMapper;
import group_10.group._0.repository.BaoCaoRepository;
import group_10.group._0.repository.BaiVietRepository;
import group_10.group._0.repository.GroupRepository;
import group_10.group._0.repository.UsersRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BaoCaoService {
    BaoCaoRepository baoCaoRepository;
    AuthenticationService authenticationService;
    BaoCaoMapper baoCaoMapper;
    UsersRepository usersRepository;
    BaiVietRepository baiVietRepository;
    GroupRepository groupRepository;
    ThongBaoService thongBaoService;

    public BaoCaoResponse createBaoCao(BaoCaoRequest request, String token)
    {

        Integer maNguoiBaoCao;

        try {
            maNguoiBaoCao = authenticationService.getMaNguoiDungFromToken(token);

        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }
        Users nguoiBaoCao = usersRepository.findById(maNguoiBaoCao).orElseThrow(
                ()->new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        if (baoCaoRepository.existsByNguoiBaoCao_MaNguoiDungAndLoaiDoiTuongAndIdDoiTuong(maNguoiBaoCao, request.getLoaiDoiTuong(), request.getIdDoiTuong()))
            throw new AppExceptions(ErrorCode.REPORT_IS_EXISTED);

        // VD: "CHUA_XU_LY", "DA_XU_LY", "BO_QUA"
        BaoCao baoCao = baoCaoMapper.toEntity(request);
        baoCao.setNguoiBaoCao(nguoiBaoCao);
        baoCao.setThoiGianTao(Instant.now());
        baoCao.setTrangThai("CHUA_XU_LY");


        return baoCaoMapper.toResponse(baoCaoRepository.save(baoCao));
    }

    public List<BaoCaoResponse> getAllBaoCao(String token) {
        try {
            // Xác thực admin từ token
            authenticationService.getRole(token);
        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        return baoCaoRepository.findAll().stream()
                .map(baoCaoMapper::toResponse)
                .toList();
    }

    public BaoCaoResponse updateTrangThaiBaoCao(Integer baoCaoId, BaoCaoUpdateRequest request, String token) {
        Integer maAdmin;
        try {
            authenticationService.getRole(token);
            maAdmin = authenticationService.getMaNguoiDungFromToken(token);
        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        BaoCao baoCao = baoCaoRepository.findById(baoCaoId)
                .orElseThrow(() -> new AppExceptions(ErrorCode.REPORT_NOT_EXISTED));

        baoCaoMapper.updateEntity(baoCao, request);
        BaoCao baoCaoDaLuu = baoCaoRepository.save(baoCao);

        // Nếu admin chọn DA_XU_LY → khoá đối tượng và gửi thông báo
        if ("DA_XU_LY".equals(request.getTrangThai())) {
            xuLyKhoaDoiTuong(baoCao.getLoaiDoiTuong(), baoCao.getIdDoiTuong(), maAdmin);
        }

        return baoCaoMapper.toResponse(baoCaoDaLuu);
    }

    /**
     * Khoá đối tượng bị báo cáo (BaiViet / Nhom / NguoiDung) và gửi thông báo cho chủ sở hữu.
     */
    private void xuLyKhoaDoiTuong(String loaiDoiTuong, Integer idDoiTuong, Integer maAdmin) {
        switch (loaiDoiTuong) {
            case "BAI_VIET" -> {
                BaiViet baiViet = baiVietRepository.findById(idDoiTuong)
                        .orElseThrow(() -> new AppExceptions(ErrorCode.BAIVIET_NOT_EXISTED));
                baiViet.setBiCam(true);
                baiVietRepository.save(baiViet);
                guiThongBao(maAdmin, baiViet.getMaNguoiDung().getMaNguoiDung(),
                        "Bài viết của bạn đã bị khoá do vi phạm tiêu chuẩn cộng đồng",
                        idDoiTuong, "BAI_VIET");
                log.info("Đã khoá bài viết ID={}", idDoiTuong);
            }
            case "NHOM" -> {
                Nhom nhom = groupRepository.findById(idDoiTuong)
                        .orElseThrow(() -> new AppExceptions(ErrorCode.GROUP_NOT_EXISTED));
                nhom.setBiCam(true);
                groupRepository.save(nhom);
                guiThongBao(maAdmin, nhom.getMaNguoiTao().getMaNguoiDung(),
                        "Nhóm của bạn đã bị khoá do vi phạm tiêu chuẩn cộng đồng",
                        idDoiTuong, "NHOM");
                log.info("Đã khoá nhóm ID={}", idDoiTuong);
            }
            case "NGUOI_DUNG" -> {
                Users user = usersRepository.findById(idDoiTuong)
                        .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
                user.setBiCam(true);
                usersRepository.save(user);
                guiThongBao(maAdmin, idDoiTuong,
                        "Tài khoản của bạn đã bị khoá do vi phạm tiêu chuẩn cộng đồng",
                        idDoiTuong, "NGUOI_DUNG");
                log.info("Đã khoá người dùng ID={}", idDoiTuong);
            }
            default -> log.warn("Loại đối tượng không hợp lệ để xử lý khoá: {}", loaiDoiTuong);
        }
    }

    private void guiThongBao(Integer maAdmin, Integer maNguoiNhan, String loaiHanhDong,
                              Integer maDoiTuong, String loaiDoiTuong) {
        try {
            thongBaoService.taoMoiThongBao(new ThongBaoRequest(
                    maAdmin, maNguoiNhan, loaiHanhDong, maDoiTuong, loaiDoiTuong));
        } catch (Exception e) {
            // Lỗi gửi thông báo không nên làm gián đoạn luồng xử lý chính
            log.error("Gửi thông báo thất bại cho userId={}: {}", maNguoiNhan, e.getMessage());
        }
    }

    public List<BaoCaoResponse> getBaoCaoByTrangThai(String trangThai, String token) {
        try {
            // Xác thực admin từ token
            authenticationService.getRole(token);
        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        return baoCaoRepository.findByTrangThai(trangThai).stream()
                .map(baoCaoMapper::toResponse)
                .toList();
    }

    public List<BaoCaoResponse> getBaoCaoByDoiTuong(String loaiDoiTuong, Integer idDoiTuong, String token) {
        try {
            // Xác thực admin từ token
            authenticationService.getRole(token);
        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        return baoCaoRepository.findByLoaiDoiTuongAndIdDoiTuong(loaiDoiTuong, idDoiTuong).stream()
                .map(baoCaoMapper::toResponse)
                .toList();
    }
    public void moKhoaDoiTuong(String loaiDoiTuong, Integer idDoiTuong, String token) {
        Integer maAdmin;
        try {
            authenticationService.getRole(token);
            maAdmin = authenticationService.getMaNguoiDungFromToken(token);
        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        switch (loaiDoiTuong) {
            case "BAI_VIET" -> {
                BaiViet baiViet = baiVietRepository.findById(idDoiTuong)
                        .orElseThrow(() -> new AppExceptions(ErrorCode.BAIVIET_NOT_EXISTED));
                baiViet.setBiCam(false);
                baiVietRepository.save(baiViet);
                guiThongBao(maAdmin, baiViet.getMaNguoiDung().getMaNguoiDung(),
                        "Bài viết của bạn đã được mở khoá", idDoiTuong, "BAI_VIET");
                log.info("Đã mở khoá bài viết ID={}", idDoiTuong);
            }
            case "NHOM" -> {
                Nhom nhom = groupRepository.findById(idDoiTuong)
                        .orElseThrow(() -> new AppExceptions(ErrorCode.GROUP_NOT_EXISTED));
                nhom.setBiCam(false);
                groupRepository.save(nhom);
                guiThongBao(maAdmin, nhom.getMaNguoiTao().getMaNguoiDung(),
                        "Nhóm của bạn đã được mở khoá", idDoiTuong, "NHOM");
                log.info("Đã mở khoá nhóm ID={}", idDoiTuong);
            }
            case "NGUOI_DUNG" -> {
                Users user = usersRepository.findById(idDoiTuong)
                        .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
                user.setBiCam(false);
                usersRepository.save(user);
                guiThongBao(maAdmin, idDoiTuong,
                        "Tài khoản của bạn đã được mở khoá", idDoiTuong, "NGUOI_DUNG");
                log.info("Đã mở khoá người dùng ID={}", idDoiTuong);
            }
            default -> throw new AppExceptions(ErrorCode.UNCATEGORIZED);
        }
    }
}
