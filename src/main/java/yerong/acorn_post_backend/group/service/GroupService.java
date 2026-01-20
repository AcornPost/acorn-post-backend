package yerong.acorn_post_backend.group.service;

import java.util.List;
import yerong.acorn_post_backend.group.dto.CreateGroupRequest;
import yerong.acorn_post_backend.group.dto.CreateGroupResponse;
import yerong.acorn_post_backend.group.dto.GroupInviteResponse;
import yerong.acorn_post_backend.group.dto.JoinGroupRequest;
import yerong.acorn_post_backend.group.dto.JoinGroupResponse;
import yerong.acorn_post_backend.group.dto.MyGroupsResponse;

public interface GroupService {
    CreateGroupResponse createGroup(Long memberId, CreateGroupRequest request);
    JoinGroupResponse joinGroup(Long memberId, JoinGroupRequest request);
    MyGroupsResponse getMyGroups(Long memberId);
    void updateGroupNickname(Long memberId, Long groupId, String newNickname);
    GroupInviteResponse getGroupByJoinCode(String joinCode);
}

