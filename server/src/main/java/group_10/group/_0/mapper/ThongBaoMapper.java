package group_10.group._0.mapper;


import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.ThongBaoResponse;
import group_10.group._0.entity.ThongBao;
import group_10.group._0.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class ThongBaoMapper {

    @Mapping(target = "maNguoiHanhDong", source = "maNguoiHanhDong.maNguoiDung")
    @Mapping(target = "maNguoiNhan", source = "maNguoiNhan.maNguoiDung")
    public abstract ThongBaoResponse toThongBaoResponse(ThongBao thongBao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "daDoc", constant = "false")
    @Mapping(target = "ngayTao", expression = "java(java.time.Instant.now())")
    @Mapping(target = "maNguoiHanhDong", ignore = true)
    @Mapping(target = "maNguoiNhan", ignore = true)
    public abstract ThongBao toThongBao(ThongBaoRequest thongBaoRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ngayTao", ignore = true)
    @Mapping(target = "maNguoiHanhDong", ignore = true)
    @Mapping(target = "maNguoiNhan", ignore = true)
    public abstract void updateThongBaoFromRequest(ThongBaoRequest thongBaoRequest, @MappingTarget ThongBao thongBao);

    @Mapping(target = "maNguoiHanhDong", source = "maNguoiHanhDong.maNguoiDung")
    @Mapping(target = "maNguoiNhan", source = "maNguoiNhan.maNguoiDung")
    public abstract ThongBaoRequest toThongBaoRequest(ThongBao thongBao);

    // Method convert Users → Integer
    protected Integer usersToInteger(Users users) {
        return users != null ? users.getMaNguoiDung() : null;
    }
}
