package group_10.group._0.mapper;

import group_10.group._0.dto.request.GroupRequest;
import group_10.group._0.dto.response.GroupResponse;
import group_10.group._0.entity.Nhom;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    // 1. Map từ Request sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "maNguoiTao", ignore = true)
    @Mapping(target = "soThanhVien", ignore = true)
    @Mapping(target = "ngayTao", ignore = true)
    @Mapping(target = "ngayCapNhat", ignore = true)
    @Mapping(target = "biCam",       ignore = true)
    Nhom toEntity(GroupRequest request);

    // 2. Map từ Entity sang Response
    @Mapping(source = "maNguoiTao.maNguoiDung", target = "maNguoiTao")
    GroupResponse toResponse(Nhom entity);

    // 3. Cập nhật Entity có sẵn từ Request
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "maNguoiTao", ignore = true)
    @Mapping(target = "soThanhVien", ignore = true)
    @Mapping(target = "ngayTao", ignore = true)
    @Mapping(target = "ngayCapNhat", ignore = true)
    @Mapping(target = "biCam",       ignore = true)
    void updateEntityFromRequest(GroupRequest request, @MappingTarget Nhom entity);
}
