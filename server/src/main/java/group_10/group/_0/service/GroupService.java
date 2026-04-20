package group_10.group._0.service;


import com.nimbusds.jose.JOSEException;
import group_10.group._0.dto.request.GroupRequest;
import group_10.group._0.dto.request.ThanhVien_GroupRequest;
import group_10.group._0.dto.request.ThanhVien_GroupUpdateRequest;
import group_10.group._0.dto.response.GroupResponse;
import group_10.group._0.dto.response.ThanhVien_GroupResponse;
import group_10.group._0.entity.Nhom;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.GroupMapper;
import group_10.group._0.repository.GroupRepository;
import group_10.group._0.repository.ThanhVien_GroupRepository;
import group_10.group._0.repository.UsersRepository;
import group_10.group._0.repository.AccessToGroupRepository;
import group_10.group._0.repository.BaiVietRepository;
import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.ThanhVienNhom;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupService {

    GroupMapper groupMapper;
    GroupRepository groupRepository;
    UsersRepository usersRepository;
    ThanhVien_GroupRepository thanhVienGroupRepository;
    AuthenticationService authenticationService;
    ThanhVien_GroupService thanhVienGroupService;
    BaiVietService baiVietService;
    BaiVietRepository baiVietRepository;
    AccessToGroupRepository accessToGroupRepository;

    public List<GroupResponse> findGroup(String key)
    {
        return groupRepository.searchByGroupName(key).stream().map(groupMapper::toResponse).toList();
    }

    public GroupResponse getGroupById(Integer id)
    {
        Nhom group = groupRepository.thongtincuaGroupById(id);
        if (group == null || Boolean.TRUE.equals(group.getBiCam()))
        {throw new AppExceptions(ErrorCode.GROUP_NOT_EXISTED);}

        return groupMapper.toResponse(group);
    }

    public List<GroupResponse> getAllDSGroup()
    {
        return groupRepository.findAllGroups().stream().map(groupMapper::toResponse).toList();
    }

    public List<GroupResponse> getGroupsByUserId(Integer userId) {
        if (!usersRepository.existsById(userId)) {
            throw new AppExceptions(ErrorCode.USER_NOT_EXISTED);
        }
        return groupRepository.findGroupsByUserId(userId).stream().map(groupMapper::toResponse).toList();
    }

    public GroupResponse createGroup(GroupRequest request, String token)  {
        Integer maNguoiTao;

        try {
            maNguoiTao = authenticationService.getMaNguoiDungFromToken(token);

        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        Users user = usersRepository.findById(maNguoiTao)
                .orElseThrow(()->new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        Nhom group = groupMapper.toEntity(request);
        group.setMaNguoiTao(user);
        group.setSoThanhVien(1);
        group.setNgayTao(Instant.now());
        group.setBiCam(false);

        groupRepository.save(group);

        ThanhVien_GroupRequest thanhVienGroupRequest = new ThanhVien_GroupRequest(
                maNguoiTao,
                group.getId(),
                maNguoiTao,
                maNguoiTao
        );

        ThanhVien_GroupResponse thanhVienGroupResponse = thanhVienGroupService.createThanhVien(thanhVienGroupRequest);
        ThanhVien_GroupUpdateRequest thanhVienGroupUpdateRequest = new ThanhVien_GroupUpdateRequest("ADMIN");

        thanhVienGroupService.updateThanhVien(thanhVienGroupUpdateRequest, thanhVienGroupResponse.getId());

        return groupMapper.toResponse(group);
    }

    public  GroupResponse updateGroup(GroupRequest request, Integer id)
    {
        Nhom nhom = groupRepository.findById(id)
                .orElseThrow(()->new AppExceptions(ErrorCode.GROUP_NOT_EXISTED));

        groupMapper.updateEntityFromRequest(request,nhom);
        nhom.setNgayCapNhat(Instant.now());

        return groupMapper.toResponse(groupRepository.save(nhom));
    }

    @Transactional
    public void deleteGroup(Integer idGroup, String token) {
        Integer maNguoiThaoTac;
        
        try {
            maNguoiThaoTac = authenticationService.getMaNguoiDungFromToken(token);
        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        Nhom nhom = groupRepository.findById(idGroup)
                .orElseThrow(() -> new AppExceptions(ErrorCode.GROUP_NOT_EXISTED));

        if (!nhom.getMaNguoiTao().getMaNguoiDung().equals(maNguoiThaoTac)) {
             throw new RuntimeException("Chỉ người tạo nhóm mới có quyền xóa nhóm");
        }

        List<BaiViet> dsBaiViet = baiVietRepository.findByMaNhom_Id(idGroup);
        if (dsBaiViet != null) {
            dsBaiViet.forEach(bv -> baiVietService.deleteBaiViet(bv.getId()));
        }

        List<ThanhVienNhom> dsThanhVien = thanhVienGroupRepository.findByMaNhom_Id(idGroup);
        if (dsThanhVien != null) {
            dsThanhVien.forEach(tv -> thanhVienGroupService.deleteThanhVien(tv.getId()));
        }

        accessToGroupRepository.deleteByMaNhom_Id(idGroup);
        
        groupRepository.deleteById(idGroup);
    }


}
