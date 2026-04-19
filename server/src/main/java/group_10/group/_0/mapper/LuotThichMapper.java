package group_10.group._0.mapper;

import group_10.group._0.dto.request.BaiVietRequest;
import group_10.group._0.dto.request.LuotThichRequest;
import group_10.group._0.dto.response.BaiVietResponse;
import group_10.group._0.dto.response.LuotThichResponse;
import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.LuotThich;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface LuotThichMapper {
    // 1. Từ Request sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "maNguoiDung", ignore = true) // Sẽ set thủ công trong Service sau khi tìm thấy User trong DB
    @Mapping(target = "ngayTao", expression = "java(java.time.Instant.now())")
    @Mapping(target = "ngayCapNhat", expression = "java(java.time.Instant.now())")
    @Mapping(target = "camXuc", source = "camXuc", defaultValue = "LIKE")
    LuotThich toLuotThich(LuotThichRequest request);

    // 2. Từ Entity sang Response
    @Mapping(target = "maNguoiDung", source = "maNguoiDung.maNguoiDung")
    @Mapping(target = "hoTen", expression = "java(luotThich.getMaNguoiDung().getHo() + \" \" + luotThich.getMaNguoiDung().getTen())")
    @Mapping(target = "anhDaiDien", source = "maNguoiDung.anhDaiDien")
    LuotThichResponse toLuotThichResponse(LuotThich luotThich);
}
