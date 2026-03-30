package group_10.group._0.service;


import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.ThongBaoResponse;
import group_10.group._0.entity.ThongBao;
import group_10.group._0.entity.Users;
import group_10.group._0.exception.AppExceptions;
import group_10.group._0.exception.ErrorCode;
import group_10.group._0.mapper.ThongBaoMapper;
import group_10.group._0.repository.ThongBaoRepository;
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
public class ThongBaoService {

    ThongBaoRepository thongBaoRepository;
    ThongBaoMapper thongBaoMapper;
    UsersRepository usersRepository;

    SseService sseService;



    public ThongBaoResponse taoMoiThongBao(ThongBaoRequest request) {
        ThongBao thongBao = thongBaoMapper.toThongBao(request);

        // Set Users object thủ công
        Users nguoiHanhDong = usersRepository.findById(request.getMaNguoiHanhDong())
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));
        Users nguoiNhan = usersRepository.findById(request.getMaNguoiNhan())
                .orElseThrow(() -> new AppExceptions(ErrorCode.USER_NOT_EXISTED));

        thongBao.setMaNguoiHanhDong(nguoiHanhDong);
        thongBao.setMaNguoiNhan(nguoiNhan);
        thongBao.setDaDoc(false);
        thongBao.setNgayTao(Instant.now());

        ThongBaoResponse response = thongBaoMapper.toThongBaoResponse(thongBaoRepository.save(thongBao));
        sseService.sendToUser(request.getMaNguoiNhan(), response);
        return response;
    }
//    public ThongBaoResponse taoMoiTHongBao(ThongBaoRequest request) {
//        ThongBao thongBao = thongBaoMapper.toThongBao(request);
//        thongBao.setDaDoc(false);
//        thongBao.setNgayTao(Instant.now());
//
//        return thongBaoMapper.toThongBaoResponse(thongBaoRepository.save(thongBao));
//
//    }

    public void xoaThongBao(Integer maThongBao)
    {

        if (!thongBaoRepository.existsById(maThongBao))
        {
            throw new AppExceptions(ErrorCode.NOTIFICATION_NOT_EXISTED);
        }
        thongBaoRepository.deleteById(maThongBao);
    }

    public void docThongBao(Integer maThongBao)
    {
        ThongBao thongBao = thongBaoRepository.findById(maThongBao)
                .orElseThrow(() -> new AppExceptions(ErrorCode.NOTIFICATION_NOT_EXISTED));
        thongBao.setDaDoc(true);
        thongBaoRepository.save(thongBao);
    }

    public List<ThongBaoResponse> layDSThongBao(Integer maUser)
    {
        return thongBaoRepository.findByMaNguoiNhan_MaNguoiDungOrderByNgayTaoDesc(maUser)
                .stream()
                .map(thongBaoMapper::toThongBaoResponse)
                .collect(Collectors.toList());
    }
}
