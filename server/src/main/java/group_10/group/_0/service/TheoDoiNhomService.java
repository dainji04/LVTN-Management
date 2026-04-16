package group_10.group._0.service;

import group_10.group._0.dto.request.TheoDoiNhomRequest;
import group_10.group._0.dto.response.TheoDoiNhomResponse;
import group_10.group._0.entity.Nhom;
import group_10.group._0.entity.TheoDoiNhom;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.TheoDoiNhomMapper;
import group_10.group._0.repository.GroupRepository;
import group_10.group._0.repository.TheoDoiNhomRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TheoDoiNhomService {
    TheoDoiNhomRepository theoDoiNhomRepository;
    TheoDoiNhomMapper theoDoiNhomMapper;
    UsersRepository usersRepository;
    GroupRepository groupRepository;

    public TheoDoiNhomResponse createTheoDoiNhom(TheoDoiNhomRequest request) {
        // Kiểm tra xem đã theo dõi chưa
        if (theoDoiNhomRepository.existsByMaNguoiTheoDoi_MaNguoiDungAndMaNhom_Id(
                request.getMaNguoiTheoDoi(), request.getMaNhom())) {
            Integer id = theoDoiNhomRepository.findIdByFollowerAndGroup(
                    request.getMaNguoiTheoDoi(), request.getMaNhom());
            return theoDoiNhomMapper.toResponse(theoDoiNhomRepository.findById(id).get());
        }

        Users nguoiTheoDoi = usersRepository.findById(request.getMaNguoiTheoDoi())
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        Nhom nhom = groupRepository.findById(request.getMaNhom())
                .orElseThrow(() -> new AppExceptions(ErrorCode.GROUP_NOT_EXISTED));

        TheoDoiNhom theoDoiNhom = theoDoiNhomMapper.toEntity(request);
        theoDoiNhom.setMaNguoiTheoDoi(nguoiTheoDoi);
        theoDoiNhom.setMaNhom(nhom);

        return theoDoiNhomMapper.toResponse(theoDoiNhomRepository.save(theoDoiNhom));
    }

    public void xoaTheoDoiNhom(Integer id) {
        if (!theoDoiNhomRepository.existsById(id)) {
            throw new AppExceptions(ErrorCode.FOLLOW_NOT_EXISTED);
        }
        theoDoiNhomRepository.deleteById(id);
    }
}
