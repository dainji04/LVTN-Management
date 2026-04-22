package group_10.group._0.mapper;

import group_10.group._0.dto.request.BinhLuanRequest;
import group_10.group._0.dto.response.BanBeResponse;
import group_10.group._0.dto.response.BinhLuanResponse;
import group_10.group._0.entity.BinhLuan;
import group_10.group._0.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface BinhLuanMapper {
    @Mapping(target = "maNguoiDung", source = "maNguoiDung.maNguoiDung")
    @Mapping(target = "hoTen", expression = "java(bl.getMaNguoiDung().getHo() + ' ' + bl.getMaNguoiDung().getTen())")
    @Mapping(target = "anhDaiDien", source = "maNguoiDung.anhDaiDien")
    @Mapping(target = "maBaiDang", source = "maBaiDang.id")
    @Mapping(target = "maBinhLuanCha", source = "maBinhLuanCha.id")
    BinhLuanResponse toResponse(BinhLuan bl);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "maNguoiDung", ignore = true)
    @Mapping(target = "maBaiDang", ignore = true)
    @Mapping(target = "maBinhLuanCha", ignore = true)
    @Mapping(target = "daChinhSua", ignore = true)
    @Mapping(target = "ngayChinhSua", ignore = true)
    @Mapping(target = "ngayTao", ignore = true)
    @Mapping(target = "ngayCapNhat", ignore = true)
    BinhLuan toEntity(BinhLuanRequest request);
}