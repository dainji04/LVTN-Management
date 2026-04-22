package group_10.group._0.mapper;

import group_10.group._0.dto.request.TheoDoiNhomRequest;
import group_10.group._0.dto.response.TheoDoiNhomResponse;
import group_10.group._0.entity.TheoDoiNhom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TheoDoiNhomMapper {

    // 1. Mapping từ Request sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "maNguoiTheoDoi", ignore = true)
    @Mapping(target = "maNhom", ignore = true)
    @Mapping(target = "ngayTheoDoi", expression = "java(java.time.Instant.now())")
    TheoDoiNhom toEntity(TheoDoiNhomRequest request);

    // 2. Mapping từ Entity sang Response
    @Mapping(source = "maNguoiTheoDoi.maNguoiDung", target = "maNguoiTheoDoi")
    @Mapping(source = "maNhom.id", target = "maNhom")
    @Mapping(source = "id", target = "id")
    TheoDoiNhomResponse toResponse(TheoDoiNhom entity);
}
