package group_10.group._0.mapper;

import group_10.group._0.dto.request.UsersRequest;
import group_10.group._0.dto.response.UsersResponse;
import group_10.group._0.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UsersMapper {
    Users toTaikhoan(UsersRequest request);
    UsersResponse toTaikhoanResponse(Users taikhoan);
    void updateTaikhoan(@MappingTarget Users user, UsersRequest request);
}
