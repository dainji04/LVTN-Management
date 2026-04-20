package group_10.group._0.service;


import com.nimbusds.jose.JOSEException;
import group_10.group._0.dto.request.BaiVietRequest;
import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.BaiVietResponse;
import group_10.group._0.dto.response.SliceResponse;
import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.HinhAnh;
import group_10.group._0.entity.Users;
import group_10.group._0.entity.*;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.BaiVietMapper;
import group_10.group._0.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BaiVietService {

    BaiVietRepository baiVietRepository;
    UsersRepository usersRepository;
    HinhAnhRepository hinhAnhRepository;
    GroupRepository groupRepository;
    ThanhVien_GroupRepository thanhVienGroupRepository;
    ThongBaoService thongBaoService;
    TheoDoiRepository theoDoiRepository;
    AuthenticationService authenticationService;
    BaiVietMapper mapper;


    // Lấy tất cả bài viết bằng phân trang
    public SliceResponse<BaiVietResponse> getAllBaiViet_PhanTrang(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<BaiViet> slice = baiVietRepository.findAllByOrderByNgayTaoDesc(pageable);

        List<BaiViet> danhSachBaiViet = slice.getContent();

        // 1. Thu thập ID bài viết
        List<Integer> ids = danhSachBaiViet.stream().map(BaiViet::getId).toList();

        // 2. Batch fetch ảnh
        Map<Integer, List<String>> anhMap = hinhAnhRepository
                .findByMaDoiTuongInAndLoaiDoiTuong(ids, "BaiViet")
                .stream()
                .collect(Collectors.groupingBy(
                        HinhAnh::getMaDoiTuong,
                        Collectors.mapping(HinhAnh::getDuongDan, Collectors.toList())
                ));

        // 3. Map sang Response
        List<BaiVietResponse> content = danhSachBaiViet.stream()
                .map(baiViet -> {
                    BaiVietResponse res = mapper.toBaiVietResponse(baiViet);
                    res.setDanhSachAnh(anhMap.getOrDefault(baiViet.getId(), List.of()));
                    return res;
                })
                .toList();

        return SliceResponse.<BaiVietResponse>builder()
                .content(content)
                .hasNext(slice.hasNext())
                .page(page)
                .size(size)
                .build();
    }



//    // Lấy tất cả bài viết
//    public List<BaiVietResponse> getAllBaiViet() {
//        return baiVietRepository.findAll()
//                .stream()
//                .map(baiViet -> getBaiVietById(baiViet.getId()))
//                .toList();
//    }

    public BaiVietResponse getBaiVietById(Integer id) {
        BaiViet baiViet = baiVietRepository.findById(id)
                .orElseThrow(() -> new AppExceptions(ErrorCode.BAIVIET_NOT_EXISTED));

        if (Boolean.TRUE.equals(baiViet.getBiCam())) {
            throw new AppExceptions(ErrorCode.BAIVIET_NOT_EXISTED);
        }

        BaiVietResponse response = mapper.toBaiVietResponse(baiViet);

        // Lấy danh sách ảnh từ HinhAnh
        List<String> danhSachAnh = hinhAnhRepository
                .findByMaDoiTuongAndLoaiDoiTuong(id, "BaiViet")
                .stream()
                .map(HinhAnh::getDuongDan)
                .toList();
        response.setDanhSachAnh(danhSachAnh);

        return response;
    }

    // Lấy tất cả bài viết của 1 user
    public SliceResponse<BaiVietResponse> getBaiVietByUser(Integer maNguoiDung, int page, int size) {
        if (!usersRepository.existsById(maNguoiDung))
            throw new AppExceptions(ErrorCode.USER_NOT_EXISTED);

        Pageable pageable = PageRequest.of(page, size);
        Slice<BaiViet> slice = baiVietRepository
                .findByMaNguoiDung_MaNguoiDungOrderByNgayTaoDesc(maNguoiDung, pageable);

        List<Integer> ids = slice.getContent().stream().map(BaiViet::getId).toList();

        Map<Integer, List<String>> anhMap = hinhAnhRepository
                .findByMaDoiTuongInAndLoaiDoiTuong(ids, "BaiViet")
                .stream()
                .collect(Collectors.groupingBy(
                        HinhAnh::getMaDoiTuong,
                        Collectors.mapping(HinhAnh::getDuongDan, Collectors.toList())
                ));

        List<BaiVietResponse> content = slice.getContent().stream()
                .map(baiViet -> {
                    BaiVietResponse res = mapper.toBaiVietResponse(baiViet);
                    res.setDanhSachAnh(anhMap.getOrDefault(baiViet.getId(), List.of()));
                    return res;
                })
                .toList();

        return SliceResponse.<BaiVietResponse>builder()
                .content(content)
                .hasNext(slice.hasNext())
                .page(page)
                .size(size)
                .build();
    }

    // Tạo bài viết mới
    public BaiVietResponse createBaiViet(BaiVietRequest request) {
        Users user = usersRepository.findById(request.getMaNguoiDung())
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        BaiViet baiViet = mapper.toBaiViet(request);
        baiViet.setMaNguoiDung(user);
        baiViet.setDaSua(false);
        baiViet.setLuotThich(0);
        baiViet.setLuotBinhLuan(0);
        baiViet.setLuotChiaSe(0);
        baiViet.setNgayTao(Instant.now());
        baiViet.setNgayCapNhat(Instant.now());
        baiViet.setBiCam(false);

        //xử lý nhom
        if (request.getMaNhom() != null)
        {
            Nhom nhom = groupRepository.findById(request.getMaNhom())
                    .orElseThrow(()->new AppExceptions(ErrorCode.GROUP_NOT_EXISTED));

            if (!(groupRepository.existsByUserIdAndGroupId(request.getMaNguoiDung(), request.getMaNhom())))
                throw new AppExceptions(ErrorCode.USER_NOT_IN_GROUP);


            baiViet.setMaNhom(nhom);
            if (Boolean.TRUE.equals(nhom.getCanDuyetBaiDang()))
            {
                baiViet.setTrangThai("PENDING");
            }
            else
                baiViet.setTrangThai("APPROVED");
        }
        else
        {
            baiViet.setTrangThai("APPROVED");

        }

        BaiViet BaiVietDaLuu = baiVietRepository.save(baiViet); // lưu bài viết trước để có ID


        if (request.getMaNhom() != null && "PENDING".equals(BaiVietDaLuu.getTrangThai()))
        {
            List<ThanhVienNhom> dsNguoiDuyet = thanhVienGroupRepository.
                    findQuanTriVienByMaNhom(request.getMaNhom());

            dsNguoiDuyet.forEach(thanhVien -> {
                        ThongBaoRequest thongBaoRequest = new ThongBaoRequest(
                                request.getMaNguoiDung(),
                                thanhVien.getMaNguoiDung().getMaNguoiDung(),
                                "yêu cầu duyet bai viet",
                                BaiVietDaLuu.getId(),
                                "BAI VIET");

                        thongBaoService.taoMoiThongBao(thongBaoRequest);
            });
        }

        if ("APPROVED".equals(BaiVietDaLuu.getTrangThai()))
        {
            List<Users> dsnguoitheodoi = theoDoiRepository.findFollowersByUserId(request.getMaNguoiDung());

            dsnguoitheodoi.forEach(users -> {
                ThongBaoRequest thongBaoRequest = new ThongBaoRequest(
                        request.getMaNguoiDung(),
                        users.getMaNguoiDung(),
                        "đăng bài viết mới",
                        BaiVietDaLuu.getId(),
                        "BAI VIET");

                thongBaoService.taoMoiThongBao(thongBaoRequest);
            });
        }

        // Lưu danh sách ảnh vào HinhAnh
        if (request.getDanhSachAnh() != null && !request.getDanhSachAnh().isEmpty()) {
            request.getDanhSachAnh().forEach(url ->
                    hinhAnhRepository.save(HinhAnh.builder()
                            .maDoiTuong(BaiVietDaLuu.getId())
                            .loaiDoiTuong("BaiViet")
                            .duongDan(url)
                            .ngayTao(Instant.now())
                            .build())
            );
        }

        return getBaiVietById(BaiVietDaLuu.getId()); // trả về kèm danh sách ảnh
    }

    //dung cho quan tri vien group (PENDING, APPROVED, REJECTED)
    public BaiVietResponse xuLyDangBaiGroup(String kqTrangThaiBaiViet ,Integer idBaiViet, String token)
    {
        Integer maNguoiTao;

        try {
            maNguoiTao = authenticationService.getMaNguoiDungFromToken(token);

        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        Users user = usersRepository.findById(maNguoiTao)
                .orElseThrow(()->new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        BaiViet baiViet = baiVietRepository.findById(idBaiViet)
                .orElseThrow(() -> new AppExceptions(ErrorCode.BAIVIET_NOT_EXISTED));

        baiViet.setTrangThai(kqTrangThaiBaiViet);
        baiViet.setNgayCapNhat(Instant.now());

        ThongBaoRequest thongBaoRequest = new ThongBaoRequest(
                maNguoiTao,
                baiViet.getMaNguoiDung().getMaNguoiDung(),
                kqTrangThaiBaiViet,
                baiViet.getId(),
                "BAI VIET"
        );
        thongBaoService.taoMoiThongBao(thongBaoRequest);

        return  mapper.toBaiVietResponse(baiVietRepository.save(baiViet));
    }

    // Cập nhật bài viết
    public BaiVietResponse updateBaiViet(Integer id, BaiVietRequest request) {
        BaiViet baiViet = baiVietRepository.findById(id)
                .orElseThrow(() -> new AppExceptions(ErrorCode.BAIVIET_NOT_EXISTED));

        mapper.updateBaiViet(baiViet, request);
        baiViet.setDaSua(true);
        baiViet.setNgayCapNhat(Instant.now());
        baiVietRepository.save(baiViet);

        // Xóa ảnh cũ và lưu ảnh mới
        if (request.getDanhSachAnh() != null) {
            hinhAnhRepository.deleteByMaDoiTuongAndLoaiDoiTuong(id, "BaiViet");
            request.getDanhSachAnh().forEach(url ->
                    hinhAnhRepository.save(HinhAnh.builder()
                            .maDoiTuong(id)
                            .loaiDoiTuong("BaiViet")
                            .duongDan(url)
                            .ngayTao(Instant.now())
                            .build())
            );
        }

        return getBaiVietById(id);
    }

    // Xóa bài viết
    public void deleteBaiViet(Integer id) {
        if (!baiVietRepository.existsById(id)) {
            throw new AppExceptions(ErrorCode.BAIVIET_NOT_EXISTED);
        }
        hinhAnhRepository.deleteByMaDoiTuongAndLoaiDoiTuong(id, "BaiViet"); // xóa ảnh trước
        baiVietRepository.deleteById(id);
    }
}