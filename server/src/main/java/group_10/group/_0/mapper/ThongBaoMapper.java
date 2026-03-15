package group_10.group._0.mapper;


import group_10.group._0.dto.request.ThongBaoRequest;
import group_10.group._0.dto.response.ThongBaoResponse;
import group_10.group._0.entity.ThongBao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ThongBaoMapper {

    // 1. Chuyển từ Entity sang Response
    ThongBaoResponse toThongBaoResponse(ThongBao thongBao);

    // 2. Chuyển từ Request sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "daDoc", constant = "false")
    @Mapping(target = "ngayTao", expression = "java(java.time.Instant.now())")
    ThongBao toThongBao(ThongBaoRequest thongBaoRequest);

    // 3. Cập nhật Entity hiện có từ Request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ngayTao", ignore = true)
    void updateThongBaoFromRequest(ThongBaoRequest thongBaoRequest, @MappingTarget ThongBao thongBao);

    ThongBaoRequest toThongBaoRequest(ThongBao thongBao);
}
