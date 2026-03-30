package group_10.group._0.mapper;

import group_10.group._0.dto.request.UsersRequest;
import group_10.group._0.dto.request.UsersUpdateRequest;
import group_10.group._0.dto.response.BanBeResponse;
import group_10.group._0.dto.response.UsersResponse;
import group_10.group._0.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface BanBeMapper {

    @Mapping(target = "ngayKetBan", ignore = true)
    BanBeResponse toBanBeResponse(Users user);
}