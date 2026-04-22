package group_10.group._0.mapper;

import group_10.group._0.dto.request.BaoCaoRequest;
import group_10.group._0.dto.request.BaoCaoUpdateRequest;
import group_10.group._0.dto.response.BaoCaoResponse;
import group_10.group._0.entity.BaoCao;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BaoCaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nguoiBaoCao", ignore = true) // nguoiBaoCao set in Service using Token/SecurityContext
    @Mapping(target = "trangThai", ignore = true)
    @Mapping(target = "thoiGianTao", ignore = true)
    BaoCao toEntity(BaoCaoRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nguoiBaoCao", ignore = true)
    @Mapping(target = "loaiDoiTuong", ignore = true)
    @Mapping(target = "idDoiTuong", ignore = true)
    @Mapping(target = "lyDo", ignore = true)
    @Mapping(target = "thoiGianTao", ignore = true)
    void updateEntity(@MappingTarget BaoCao entity, BaoCaoUpdateRequest request);

    @Mapping(source = "nguoiBaoCao.maNguoiDung", target = "nguoiBaoCaoId")
    BaoCaoResponse toResponse(BaoCao entity);
}
