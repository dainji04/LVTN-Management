package group_10.group._0.mapper;

import group_10.group._0.dto.request.TheoDoiRequest;
import group_10.group._0.dto.response.TheoDoiResponse;
import group_10.group._0.entity.TheoDoi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface TheoDoiMapper {

    // 1. Mapping từ Request sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ngayTheoDoi", expression = "java(java.time.Instant.now())")
    TheoDoi toEntity(TheoDoiRequest request);

    // 2. Mapping từ Entity sang Response
    @Mapping(source = "maNguoiTheoDoi.maNguoiDung", target = "maNguoiTheoDoi")
    @Mapping(source = "maNguoiDuocTheoDoi", target = "maNguoiDuocTheoDoi")
    @Mapping(source = "id", target = "id")
    TheoDoiResponse toResponse(TheoDoi entity);
}
