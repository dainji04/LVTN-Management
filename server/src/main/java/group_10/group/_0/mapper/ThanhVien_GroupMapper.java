package group_10.group._0.mapper;

import group_10.group._0.dto.request.ThanhVien_GroupRequest;
import group_10.group._0.dto.request.ThanhVien_GroupUpdateRequest;
import group_10.group._0.dto.response.ThanhVien_GroupResponse;
import group_10.group._0.entity.ThanhVienNhom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ThanhVien_GroupMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vaiTro", ignore = true)
    @Mapping(target = "ngayThamGia", ignore = true)
    @Mapping(target = "maNguoiDung.maNguoiDung", source = "maNguoiDung")
    @Mapping(target = "maNhom.id", source = "maNhom")
    @Mapping(target = "duocMoiBoi.maNguoiDung", source = "duocMoiBoi")
    @Mapping(target = "chapNhanBoi.maNguoiDung", source = "chapNhanBoi")
    ThanhVienNhom toEntity(ThanhVien_GroupRequest request);

    @Mapping(source = "maNguoiDung.maNguoiDung", target = "maNguoiDung")
    @Mapping(source = "maNhom.id", target = "maNhom")
    @Mapping(source = "duocMoiBoi.maNguoiDung", target = "duocMoiBoi")
    @Mapping(source = "chapNhanBoi.maNguoiDung", target = "chapNhanBoi")
    ThanhVien_GroupResponse toResponse(ThanhVienNhom entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "maNguoiDung", ignore = true)
    @Mapping(target = "maNhom", ignore = true)
    @Mapping(target = "ngayThamGia", ignore = true)
    @Mapping(target = "duocMoiBoi", ignore = true)
    @Mapping(target = "chapNhanBoi", ignore = true)
    void updateEntityFromRequest(ThanhVien_GroupUpdateRequest request, @MappingTarget ThanhVienNhom entity);

}
