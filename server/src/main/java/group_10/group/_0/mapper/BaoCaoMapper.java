package group_10.group._0.mapper;

import group_10.group._0.dto.request.BaoCaoRequest;
import group_10.group._0.dto.response.BaoCaoResponse;
import group_10.group._0.entity.BaoCao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BaoCaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nguoiBaoCao", ignore = true) // nguoiBaoCao set in Service using Token/SecurityContext
    @Mapping(target = "trangThai", ignore = true)
    @Mapping(target = "thoiGianTao", ignore = true)
    BaoCao toEntity(BaoCaoRequest request);

    @Mapping(source = "nguoiBaoCao.maNguoiDung", target = "nguoiBaoCaoId")
    BaoCaoResponse toResponse(BaoCao entity);
}
