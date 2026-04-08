package group_10.group._0.service;

import group_10.group._0.dto.request.BaiVietRequest;
import group_10.group._0.dto.response.BaiVietResponse;
import group_10.group._0.dto.response.SliceResponse;
import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.HinhAnh;
import group_10.group._0.entity.Users;
import group_10.group._0.mapper.BaiVietMapper;
import group_10.group._0.repository.BaiVietRepository;
import group_10.group._0.repository.HinhAnhRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

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

    // Lấy bài viết theo ID
    public BaiVietResponse getBaiVietById(Integer id) {
        BaiViet baiViet = baiVietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại: " + id));

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


    public SliceResponse<BaiVietResponse> getBaiVietByUser(Integer maNguoiDung, int page, int size) {
        if (!usersRepository.existsById(maNguoiDung))
            throw new RuntimeException("User không tồn tại: " + maNguoiDung);

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

    // Lấy tất cả bài viết của 1 user
    public List<BaiVietResponse> getBaiVietByUser(Integer maNguoiDung) {
        // Kiểm tra user có tồn tại không
        if (!usersRepository.existsById(maNguoiDung)) {
            throw new RuntimeException("User không tồn tại: " + maNguoiDung);
        }

        return baiVietRepository.findByMaNguoiDung_MaNguoiDung(maNguoiDung)
                .stream()
                .map(baiViet -> getBaiVietById(baiViet.getId()))
                .toList();
    }

    // Tạo bài viết mới
    public BaiVietResponse createBaiViet(BaiVietRequest request) {
        Users user = usersRepository.findById(request.getMaNguoiDung())
                .orElseThrow(() -> new RuntimeException("User không tồn tại: " + request.getMaNguoiDung()));

        BaiViet baiViet = mapper.toBaiViet(request);
        baiViet.setMaNguoiDung(user);
        baiViet.setDaSua(false);
        baiViet.setLuotThich(0);
        baiViet.setLuotBinhLuan(0);
        baiViet.setLuotChiaSe(0);
        baiViet.setNgayTao(Instant.now());
        baiViet.setNgayCapNhat(Instant.now());

        BaiViet BaiVietDaLuu = baiVietRepository.save(baiViet); // lưu bài viết trước để có ID

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

    // Cập nhật bài viết
    public BaiVietResponse updateBaiViet(Integer id, BaiVietRequest request) {
        BaiViet baiViet = baiVietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại: " + id));

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
            throw new RuntimeException("Bài viết không tồn tại: " + id);
        }
        hinhAnhRepository.deleteByMaDoiTuongAndLoaiDoiTuong(id, "BaiViet"); // xóa ảnh trước
        baiVietRepository.deleteById(id);
    }
}