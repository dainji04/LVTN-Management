package group_10.group._0.service;

import group_10.group._0.dto.request.BinhLuanRequest;
import group_10.group._0.dto.response.BinhLuanResponse;
import group_10.group._0.dto.response.SliceResponse;
import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.BinhLuan;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.BinhLuanMapper;
import group_10.group._0.repository.BaiVietRepository;
import group_10.group._0.repository.BinhLuanRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BinhLuanService {

    final BinhLuanRepository binhLuanRepository;
    final UsersRepository usersRepository;
    final BaiVietRepository baiVietRepository;
    final BinhLuanMapper mapper;

    public BinhLuanResponse createBinhLuan(BinhLuanRequest request) {
        Users user = usersRepository.findById(request.getMaNguoiDung())
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        BaiViet baiViet = baiVietRepository.findById(request.getMaBaiDang())
                .orElseThrow(() -> new AppExceptions(ErrorCode.BAIVIET_NOT_EXISTED));

        BinhLuan binhLuan = mapper.toEntity(request);
        binhLuan.setMaNguoiDung(user);
        binhLuan.setMaBaiDang(baiViet);
        binhLuan.setDaChinhSua(false);
        binhLuan.setNgayTao(Instant.now());
        binhLuan.setNgayCapNhat(Instant.now());

        // Set bình luận cha nếu là reply
        if (request.getMaBinhLuanCha() != null) {
            BinhLuan cha = binhLuanRepository.findById(request.getMaBinhLuanCha())
                    .orElseThrow(() -> new AppExceptions(ErrorCode.BINHLUANCHA_NOT_EXISTED));
            binhLuan.setMaBinhLuanCha(cha);
        }

        // Tăng số bình luận
        baiViet.setLuotBinhLuan((baiViet.getLuotBinhLuan() == null ? 0 : baiViet.getLuotBinhLuan()) + 1);
        baiVietRepository.save(baiViet);

        return mapper.toResponse(binhLuanRepository.save(binhLuan));
    }

    public SliceResponse<BinhLuanResponse> getByBaiViet(Integer maBaiDang, int page, int size) {
        BaiViet baiViet = baiVietRepository.findById(maBaiDang)
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại!"));

        Pageable pageable = PageRequest.of(page, size);
        Slice<BinhLuan> slice = binhLuanRepository.findByMaBaiDangOrderByNgayTaoAsc(baiViet, pageable);

        List<BinhLuanResponse> content = slice.getContent()
                .stream().map(mapper::toResponse).toList();

        return SliceResponse.<BinhLuanResponse>builder()
                .content(content)
                .hasNext(slice.hasNext())
                .page(page)
                .size(size)
                .build();
    }

    public SliceResponse<BinhLuanResponse> getReplies(Integer maBinhLuanCha, int page, int size) {
        BinhLuan cha = binhLuanRepository.findById(maBinhLuanCha)
                .orElseThrow(() -> new RuntimeException("Bình luận không tồn tại!"));

        Pageable pageable = PageRequest.of(page, size);
        Slice<BinhLuan> slice = binhLuanRepository.findByMaBinhLuanChaOrderByNgayTaoAsc(cha, pageable);

        return SliceResponse.<BinhLuanResponse>builder()
                .content(slice.getContent().stream().map(mapper::toResponse).toList())
                .hasNext(slice.hasNext())
                .page(page)
                .size(size)
                .build();
    }

    public BinhLuanResponse updateBinhLuan(Integer id, String noiDung) {
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new AppExceptions(ErrorCode.BINHLUAN_NOT_EXISTED));
        binhLuan.setNoiDung(noiDung);
        binhLuan.setDaChinhSua(true);
        binhLuan.setNgayChinhSua(Instant.now());
        binhLuan.setNgayCapNhat(Instant.now());
        return mapper.toResponse(binhLuanRepository.save(binhLuan));
    }

    public void deleteBinhLuan(Integer id, Integer nguoiXoaId) {
        BinhLuan binhLuan = binhLuanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bình luận không tồn tại!"));

        // Kiểm tra quyền xóa:
        // 1. Chủ bình luận có thể xóa bình luận của mình
        // 2. Chủ bài viết có thể xóa bình luận của người khác
        boolean laoChuBinhLuan = binhLuan.getMaNguoiDung().getMaNguoiDung().equals(nguoiXoaId);
        boolean laChuBaiViet = binhLuan.getMaBaiDang().getMaNguoiDung().getMaNguoiDung().equals(nguoiXoaId);

        if (!laoChuBinhLuan && !laChuBaiViet)
            throw new RuntimeException("Không có quyền xóa bình luận này!");

        // Giảm số bình luận
        BaiViet baiViet = binhLuan.getMaBaiDang();
        int current = baiViet.getLuotBinhLuan() == null ? 0 : baiViet.getLuotBinhLuan();
        baiViet.setLuotBinhLuan(Math.max(0, current - 1));
        baiVietRepository.save(baiViet);

        binhLuanRepository.deleteById(id);
    }
}