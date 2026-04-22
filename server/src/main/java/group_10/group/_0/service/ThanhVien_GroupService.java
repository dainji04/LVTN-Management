package group_10.group._0.service;


import group_10.group._0.dto.request.ThanhVien_GroupRequest;
import group_10.group._0.dto.request.ThanhVien_GroupUpdateRequest;
import group_10.group._0.dto.response.ThanhVien_GroupResponse;
import group_10.group._0.entity.ThanhVienNhom;
import group_10.group._0.entity.TheoDoi;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.ThanhVien_GroupMapper;
import group_10.group._0.repository.ThanhVien_GroupRepository;
import group_10.group._0.repository.TheoDoiNhomRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ThanhVien_GroupService {
     ThanhVien_GroupMapper thanhVienGroupMapper;
     ThanhVien_GroupRepository thanhVienGroupRepository;
     TheoDoiNhomService theoDoiNhomService;
     UsersRepository usersRepository;
     TheoDoiNhomRepository theoDoiNhomRepository;

    public ThanhVien_GroupResponse createThanhVien(ThanhVien_GroupRequest request)
    {
       if (!usersRepository.existsById(request.getMaNguoiDung()))
       {
           throw new AppExceptions(ErrorCode.USER_NOT_EXISTED);
       }

       ThanhVienNhom thanhVienNhom = thanhVienGroupMapper.toEntity(request);
       thanhVienNhom.setVaiTro("MEMBER");
       thanhVienNhom.setNgayThamGia(Instant.now());

       if (request.getDuocMoiBoi() != null) {
           thanhVienNhom.setDuocMoiBoi(
               usersRepository.findById(request.getDuocMoiBoi())
                   .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED))
           );
       }
       if (request.getChapNhanBoi() != null) {
           thanhVienNhom.setChapNhanBoi(
               usersRepository.findById(request.getChapNhanBoi())
                   .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED))
           );
       }

       return thanhVienGroupMapper.toResponse(thanhVienGroupRepository.save(thanhVienNhom));
    }

    public void deleteThanhVien(Integer id)
    {

       ThanhVienNhom thanhVienNhom =  thanhVienGroupRepository.findById(id)
               .orElseThrow(()->new AppExceptions(ErrorCode.MEMBER_NOT_EXISTED));

       Integer matheoDoi = theoDoiNhomRepository.findIdByFollowerAndGroup(
               thanhVienNhom.getMaNguoiDung().getMaNguoiDung(),
                thanhVienNhom.getMaNhom().getId());

        if (matheoDoi != null) {
            theoDoiNhomService.xoaTheoDoiNhom(matheoDoi);
        }

       thanhVienGroupRepository.deleteById(id);


    }

    public ThanhVien_GroupResponse updateThanhVien(ThanhVien_GroupUpdateRequest request, Integer id)
    {
        ThanhVienNhom thanhVienNhom = thanhVienGroupRepository.findById(id).
                orElseThrow(()-> new AppExceptions(ErrorCode.MEMBER_NOT_EXISTED));

        thanhVienGroupMapper.updateEntityFromRequest(request,thanhVienNhom);

        return thanhVienGroupMapper.toResponse(thanhVienGroupRepository.save(thanhVienNhom));
    }

    public List<ThanhVien_GroupResponse> DSThanhVien(Integer idGroup)
    {
        return thanhVienGroupRepository.findByMaNhom_Id(idGroup)
                .stream()
                .map(thanhVienGroupMapper ::toResponse)
                .collect(Collectors.toList());
    }

    public Long soThanhVienTrongGroup(Integer idgroup)
    {
        return thanhVienGroupRepository.countByMaNhom_Id(idgroup);
    }
}
