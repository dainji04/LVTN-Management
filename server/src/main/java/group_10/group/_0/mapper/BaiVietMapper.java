package group_10.group._0.mapper;

import group_10.group._0.dto.request.BaiVietRequest;
import group_10.group._0.dto.request.UsersRequest;
import group_10.group._0.dto.response.BaiVietResponse;
import group_10.group._0.dto.response.UsersResponse;
import group_10.group._0.entity.BaiViet;
import group_10.group._0.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface BaiVietMapper {
    // Entity -> Response
    @Mapping(target = "maBaiViet",    source = "id")
    @Mapping(target = "maNguoiDung",  source = "maNguoiDung.maNguoiDung")
    @Mapping(target = "hoTen",        expression = "java(baiViet.getMaNguoiDung().getHo() + ' ' + baiViet.getMaNguoiDung().getTen())")
    @Mapping(target = "anhDaiDienNguoiDang", source = "maNguoiDung.anhDaiDien")//Anh dai dien cua nguoi dang
    @Mapping(target = "danhSachAnh", ignore = true) // set thủ công trong Service
    BaiVietResponse toBaiVietResponse(BaiViet baiViet);

    // Request -> Entity (maNguoiDung sẽ set thủ công trong Service)
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "maNguoiDung",  ignore = true)
    @Mapping(target = "daSua",        ignore = true)
    @Mapping(target = "luotThich",    ignore = true)
    @Mapping(target = "luotBinhLuan", ignore = true)
    @Mapping(target = "luotChiaSe",   ignore = true)
    @Mapping(target = "ngayTao",      ignore = true)
    @Mapping(target = "ngayCapNhat",  ignore = true)
    BaiViet toBaiViet(BaiVietRequest request);

    // Update Entity từ Request
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "maNguoiDung",  ignore = true)
    @Mapping(target = "daSua",        ignore = true)
    @Mapping(target = "luotThich",    ignore = true)
    @Mapping(target = "luotBinhLuan", ignore = true)
    @Mapping(target = "luotChiaSe",   ignore = true)
    @Mapping(target = "ngayTao",      ignore = true)
    @Mapping(target = "ngayCapNhat",  ignore = true)
    void updateBaiViet(@MappingTarget BaiViet baiViet, BaiVietRequest request);
}
