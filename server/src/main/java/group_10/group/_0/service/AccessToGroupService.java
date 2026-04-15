package group_10.group._0.service;


import com.nimbusds.jose.JOSEException;
import group_10.group._0.dto.request.*;
import group_10.group._0.dto.response.AccessToGroupResponse;
import group_10.group._0.entity.Nhom;
import group_10.group._0.entity.ThanhVienNhom;
import group_10.group._0.entity.Users;
import group_10.group._0.entity.YeuCauThamGiaNhom;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.AccessToGroupMapper;
import group_10.group._0.repository.AccessToGroupRepository;
import group_10.group._0.repository.GroupRepository;
import group_10.group._0.repository.ThanhVien_GroupRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessToGroupService {
    AccessToGroupRepository accessToGroupRepository;
    AccessToGroupMapper accessToGroupMapper;
    ThanhVien_GroupRepository thanhVienGroupRepository;
    ThanhVien_GroupService thanhVienGroupService;
    ThongBaoService thongBaoService;
    AuthenticationService authenticationService;
    UsersRepository usersRepository;
    TheoDoiService theoDoiService;
    GroupRepository groupRepository;


    //người dùng
    public AccessToGroupResponse createRequestToGroup(AccessToGroupRequest request)
    {

        Nhom nhom = groupRepository.findById(request.getMaNhom())
                .orElseThrow(()->new AppExceptions(ErrorCode.GROUP_NOT_EXISTED));
        if ("PUBLIC".equals(nhom.getLoaiNhom()))
        {
            YeuCauThamGiaNhom yeuCauThamGiaNhom = accessToGroupMapper.toEntity(request);
            yeuCauThamGiaNhom.setTrangThai("CHAP_NHAN");
            yeuCauThamGiaNhom.setYeuCauLuc(Instant.now());

            accessToGroupRepository.save(yeuCauThamGiaNhom);

            ThanhVien_GroupRequest thanhVienGroup = new ThanhVien_GroupRequest(
                    request.getMaNguoiDung(),
                    request.getMaNhom(),
                    null,
                    null
            );


//            thanhVienGroup.setDuocMoiBoi(null);
//            thanhVienGroup.setChapNhanBoi(null);

            thanhVienGroupService.createThanhVien(thanhVienGroup);

            TheoDoiRequest theoDoiRequest = new TheoDoiRequest(
                    request.getMaNguoiDung(),
                    request.getMaNhom()
            );
            theoDoiService.createTheoDoiKhongThongBao(theoDoiRequest);

            List<ThanhVienNhom> dsNguoiDuyet = thanhVienGroupRepository.
                    findQuanTriVienByMaNhom(request.getMaNhom());

            dsNguoiDuyet.forEach(thanhVien -> {
                ThongBaoRequest thongBaoRequest = new ThongBaoRequest(
                        request.getMaNguoiDung(),
                        thanhVien.getMaNguoiDung().getMaNguoiDung(),
                        "đã tham gia nhóm",
                        yeuCauThamGiaNhom.getId(),
                        "ACCESS TO GROUP"
                );
                thongBaoService.taoMoiThongBao(thongBaoRequest);
            });

            return accessToGroupMapper.toResponse(yeuCauThamGiaNhom);
        }

        YeuCauThamGiaNhom yeuCauThamGiaNhom = accessToGroupMapper.toEntity(request);
        yeuCauThamGiaNhom.setTrangThai("DA_GUI");
        yeuCauThamGiaNhom.setYeuCauLuc(Instant.now());


        accessToGroupRepository.save(yeuCauThamGiaNhom);

        List<ThanhVienNhom> dsNguoiDuyet = thanhVienGroupRepository.
                findQuanTriVienByMaNhom(request.getMaNhom());

        dsNguoiDuyet.forEach(thanhVien -> {
            ThongBaoRequest thongBaoRequest = new ThongBaoRequest(
                    request.getMaNguoiDung(),
                    thanhVien.getMaNguoiDung().getMaNguoiDung(),
                    "yêu cầu tham gia nhóm",
                    yeuCauThamGiaNhom.getId(),
                    "ACCESS TO GROUP"
            );
            thongBaoService.taoMoiThongBao(thongBaoRequest);
        });

        return accessToGroupMapper.toResponse(yeuCauThamGiaNhom);
    }

//    public void handleRequestToGroup(Integer id, AccessToGroupUpdateRequest request)
//    {
//
//    }

    // quan tri vien
    public List<AccessToGroupResponse> DSYeuCauChuaXuLy(Integer idGroup)
    {
        return accessToGroupRepository.findByMaNhom_IdAndTrangThai(idGroup, "DA_GUI")
                .stream()
                .map(accessToGroupMapper ::toResponse)
                .toList();
    }
//người dùng
    public void huyYeuCau(Integer idYeuCau)
    {
        if (!accessToGroupRepository.existsById(idYeuCau))
        {
            throw new AppExceptions(ErrorCode.ACCESS_NOT_EXISTED);
        }
        accessToGroupRepository.deleteById(idYeuCau);
    }

    //dung bởi quản trị viên (chap nhận)
    public void accept(Integer id,AccessToGroupUpdateRequest request,String token)
    {
        YeuCauThamGiaNhom yeuCauThamGiaNhom = accessToGroupRepository.findById(id)
                .orElseThrow(()-> new AppExceptions(ErrorCode.ACCESS_NOT_EXISTED));

        Integer maNguoiDuyet;

        try {
            maNguoiDuyet = authenticationService.getMaNguoiDungFromToken(token);

        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        if ("CHAP_NHAN".equals(yeuCauThamGiaNhom.getTrangThai())|| "TU_CHOI".equals(yeuCauThamGiaNhom.getTrangThai())  )
            throw new AppExceptions(ErrorCode.REQUEST_IS_PROCESSED);

        Users user = usersRepository.findById(maNguoiDuyet)
                .orElseThrow(()->new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        yeuCauThamGiaNhom.setXuLyLuc(Instant.now());
        yeuCauThamGiaNhom.setTrangThai(request.getTrangThai());
        yeuCauThamGiaNhom.setXuLyBoi(user);

        accessToGroupRepository.save(yeuCauThamGiaNhom);


        ThanhVien_GroupRequest thanhVienGroup = new ThanhVien_GroupRequest(
                yeuCauThamGiaNhom.getMaNguoiDung().getMaNguoiDung(),
                yeuCauThamGiaNhom.getMaNhom().getId(),
                null,
                maNguoiDuyet
        );
        thanhVienGroupService.createThanhVien(thanhVienGroup);

        TheoDoiRequest theoDoiRequest = new TheoDoiRequest(
                yeuCauThamGiaNhom.getMaNguoiDung().getMaNguoiDung(),
                yeuCauThamGiaNhom.getMaNhom().getId()
        );
        theoDoiService.createTheoDoi(theoDoiRequest);

        ThongBaoRequest thongBaoRequest = new ThongBaoRequest(
                maNguoiDuyet,
                yeuCauThamGiaNhom.getMaNguoiDung().getMaNguoiDung(),
                "chấp nhận yêu cầu tham gia nhóm",
                yeuCauThamGiaNhom.getId(),
                "ACCEPT TO GROUP"
        );
        thongBaoService.taoMoiThongBao(thongBaoRequest);
    }

    //dung bởi quản trị viên (từ chối)
    public void reject(Integer id,AccessToGroupUpdateRequest request,String token)
    {
        YeuCauThamGiaNhom yeuCauThamGiaNhom = accessToGroupRepository.findById(id)
                .orElseThrow(()-> new AppExceptions(ErrorCode.ACCESS_NOT_EXISTED));

        Integer maNguoiDuyet;

        try {
            maNguoiDuyet = authenticationService.getMaNguoiDungFromToken(token);

        } catch (ParseException | JOSEException e) {
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        }

        if ("CHAP_NHAN".equals(yeuCauThamGiaNhom.getTrangThai())|| "TU_CHOI".equals(yeuCauThamGiaNhom.getTrangThai())  )
            throw new AppExceptions(ErrorCode.REQUEST_IS_PROCESSED);

        Users user = usersRepository.findById(maNguoiDuyet)
                .orElseThrow(()->new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        yeuCauThamGiaNhom.setXuLyLuc(Instant.now());
        yeuCauThamGiaNhom.setTrangThai(request.getTrangThai());
        yeuCauThamGiaNhom.setXuLyBoi(user);

        accessToGroupRepository.save(yeuCauThamGiaNhom);

        ThongBaoRequest thongBaoRequest = new ThongBaoRequest(
                maNguoiDuyet,
                yeuCauThamGiaNhom.getMaNguoiDung().getMaNguoiDung(),
                "từ chối yêu cầu tham gia nhóm",
                yeuCauThamGiaNhom.getId(),
                "REJECT TO GROUP"
        );
        thongBaoService.taoMoiThongBao(thongBaoRequest);
    }

}
