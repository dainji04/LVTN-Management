package group_10.group._0.service;


import group_10.group._0.dto.request.GroupRequest;
import group_10.group._0.dto.response.GroupResponse;
import group_10.group._0.mapper.GroupMapper;
import group_10.group._0.repository.GroupRepository;
import group_10.group._0.repository.ThanhVien_GroupRepository;
import group_10.group._0.repository.UsersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupService {

    GroupMapper groupMapper;
    GroupRepository groupRepository;
    UsersRepository usersRepository;
    ThanhVien_GroupRepository thanhVienGroupRepository;

    public GroupResponse createGroup(GroupRequest request)
    {

    }
}
