package group_10.group._0.mapper;

import group_10.group._0.dto.request.BaiVietRequest;
import group_10.group._0.dto.response.BaiVietResponse;
import group_10.group._0.entity.BaiViet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;


@Mapper(componentModel = "spring")
public interface BaiVietMapper {

    // 1. Entity -> Response
    @Mapping(target = "maBaiViet",    source = "id")
    @Mapping(target = "maNguoiDung",  source = "maNguoiDung.maNguoiDung")
    // Map ID từ đối tượng Nhom sang Integer maNhom trong Response
    @Mapping(target = "maNhom",       source = "maNhom.id")
    @Mapping(target = "trangThai",    source = "trangThai")
    @Mapping(target = "hoTen",        expression = "java(baiViet.getMaNguoiDung().getHo() + \" \" + baiViet.getMaNguoiDung().getTen())")
    @Mapping(target = "anhDaiDienNguoiDang", source = "maNguoiDung.anhDaiDien")
    @Mapping(target = "danhSachAnh",  ignore = true) // set thủ công trong Service
    BaiVietResponse toBaiVietResponse(BaiViet baiViet);

    // 2. Request -> Entity
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "maNguoiDung",  ignore = true) // Set thủ công Users object trong Service
    // Map Integer maNhom từ Request vào ID của đối tượng Nhom trong Entity
    @Mapping(target = "maNhom.id",    source = "maNhom")
    @Mapping(target = "trangThai",    ignore = true) // Set thủ công dựa trên logic canDuyetBaiDang của Group
    @Mapping(target = "daSua",        ignore = true)
    @Mapping(target = "luotThich",    ignore = true)
    @Mapping(target = "luotBinhLuan", ignore = true)
    @Mapping(target = "luotChiaSe",   ignore = true)
    @Mapping(target = "ngayTao",      ignore = true)
    @Mapping(target = "ngayCapNhat",  ignore = true)
    BaiViet toBaiViet(BaiVietRequest request);

    // 3. Update Entity từ Request
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",           ignore = true)
    @Mapping(target = "maNguoiDung",  ignore = true)
    @Mapping(target = "maNhom",       ignore = true) // Thường không cập nhật lại nhóm cho bài viết đã đăng
    @Mapping(target = "trangThai",    ignore = true)
    @Mapping(target = "daSua",        ignore = true)
    @Mapping(target = "luotThich",    ignore = true)
    @Mapping(target = "luotBinhLuan", ignore = true)
    @Mapping(target = "luotChiaSe",   ignore = true)
    @Mapping(target = "ngayTao",      ignore = true)
    @Mapping(target = "ngayCapNhat",  ignore = true)
    void updateBaiViet(@MappingTarget BaiViet baiViet, BaiVietRequest request);
}