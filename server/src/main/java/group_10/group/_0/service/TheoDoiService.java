package group_10.group._0.service;

import group_10.group._0.dto.request.TheoDoiRequest;
import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.SoLuongTheoDoiResponse;
import group_10.group._0.dto.response.TheoDoiResponse;
import group_10.group._0.entity.TheoDoi;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.TheoDoiMapper;
import group_10.group._0.repository.TheoDoiRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TheoDoiService {

    TheoDoiRepository theoDoiRepository;
    UsersRepository usersRepository;
    TheoDoiMapper theoDoiMapper;
    ThongBaoService thongBaoService;

    public void xoaTheoDoi(Integer id)
    {
        if (!theoDoiRepository.existsById(id))
        {
            throw new AppExceptions(ErrorCode.NOTIFICATION_NOT_EXISTED);
        }
        theoDoiRepository.deleteById(id);
    }

    public SoLuongTheoDoiResponse soLuongFollow(Integer maUser)
    {
        Users users = usersRepository.findById(maUser)
                .orElseThrow( () -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        long TongTheoDoi = theoDoiRepository.countByMaNguoiDuocTheoDoi(maUser);
        long TongDuocTheoDoi = theoDoiRepository.countByMaNguoiTheoDoi(users);

        List<Users> DSDuocTheoDoi = theoDoiRepository.findFollowersByUserId(maUser);
        List<Users> DSDangTheoDoi = theoDoiRepository.findFollowingByUser(users);

        return SoLuongTheoDoiResponse.builder()
                .tongDangTheoDoi(TongTheoDoi)
                .tongNguoiTheoDoi(TongDuocTheoDoi)
                .danhSachDangTheoDoi(DSDangTheoDoi)
                .danhSachNguoiTheoDoi(DSDuocTheoDoi)
                .build();
    }

    public TheoDoiResponse createTheodoi(TheoDoiRequest request)
    {
       TheoDoi theoDoi = theoDoiMapper.toEntity(request);
       Users nguoiTheoDoi = usersRepository.findById(request.getMaNguoiTheoDoi())
               .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
       theoDoi.setMaNguoiTheoDoi(nguoiTheoDoi);
       return theoDoiMapper.toResponse(theoDoiRepository.save(theoDoi));
    }

    public TheoDoi getTheoDoiByID(Integer id)
    {
        if (!theoDoiRepository.existsById(id))
        {
            throw new AppExceptions(ErrorCode.FOLLOW_NOT_EXISTED);
        }
        return theoDoiRepository.findById(id).get();
    }

    public void createTheoDoiKhongThongBao(TheoDoiRequest request) {
        TheoDoi theoDoi = theoDoiMapper.toEntity(request);
        Users nguoiTheoDoi = usersRepository.findById(request.getMaNguoiTheoDoi())
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        theoDoi.setMaNguoiTheoDoi(nguoiTheoDoi);
        theoDoiRepository.save(theoDoi);
    }
}
