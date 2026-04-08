package group_10.group._0.mapper;

import group_10.group._0.dto.request.LoiMoiRequest;
import group_10.group._0.dto.request.UsersRequest;
import group_10.group._0.dto.request.UsersUpdateRequest;
import group_10.group._0.dto.response.LoiMoiResponse;
import group_10.group._0.dto.response.UsersResponse;
import group_10.group._0.entity.LoiMoiKetBan;
import group_10.group._0.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface LoiMoiKetBanMapper {

    @Mapping(target = "maNguoiGui", source = "maNguoiGui.maNguoiDung")
    @Mapping(target = "hoTenNguoiGui", expression = "java(loiMoi.getMaNguoiGui().getHo() + \" \" + loiMoi.getMaNguoiGui().getTen())")
    @Mapping(target = "anhDaiDienNguoiGui", source = "maNguoiGui.anhDaiDien")
    @Mapping(target = "maNguoiNhan", source = "maNguoiNhan.maNguoiDung")
    LoiMoiResponse toResponse(LoiMoiKetBan loiMoi);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trangThai", constant = "DA_GUI")
    @Mapping(target = "maNguoiGui", ignore = true)
    @Mapping(target = "maNguoiNhan", ignore = true)
    LoiMoiKetBan toEntity(LoiMoiRequest request);
}