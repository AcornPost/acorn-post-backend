package yerong.acorn_post_backend.group.dto;

import yerong.acorn_post_backend.group.domain.GroupMemberRole;
import yerong.acorn_post_backend.group.domain.GroupType;

public record JoinGroupResponse(
        Long groupId,
        String groupName,
        GroupType type,
        GroupMemberRole role,
        boolean alreadyJoined
) {}