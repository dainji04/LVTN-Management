package group_10.group._0.mapper;

import group_10.group._0.dto.request.AccessToGroupRequest;
import group_10.group._0.dto.request.AccessToGroupUpdateRequest;
import group_10.group._0.dto.response.AccessToGroupResponse;
import group_10.group._0.entity.YeuCauThamGiaNhom;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccessToGroupMapper {

    // 1. Map từ Request sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "yeuCauLuc", ignore = true)
    @Mapping(target = "trangThai", ignore = true)
    @Mapping(target = "xuLyBoi", ignore = true)
    @Mapping(target = "xuLyLuc", ignore = true)
    @Mapping(target = "maNhom.id", source = "maNhom")
    @Mapping(target = "maNguoiDung.maNguoiDung", source = "maNguoiDung")
    YeuCauThamGiaNhom toEntity(AccessToGroupRequest request);

    // 2. Map từ Entity sang Response
    @Mapping(source = "maNhom.id", target = "maNhom")
    @Mapping(source = "maNguoiDung.maNguoiDung", target = "maNguoiDung")
    @Mapping(source = "xuLyBoi.maNguoiDung", target = "xuLyBoi")
    AccessToGroupResponse toResponse(YeuCauThamGiaNhom entity);

    // 3. Cập nhật Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "yeuCauLuc", ignore = true)
    @Mapping(target = "maNhom", ignore = true)
    @Mapping(target = "maNguoiDung", ignore = true)
    @Mapping(target = "xuLyBoi", ignore = true)
    @Mapping(target = "xuLyLuc", ignore = true)
    void updateEntityFromRequest(AccessToGroupUpdateRequest request, @MappingTarget YeuCauThamGiaNhom entity);
}