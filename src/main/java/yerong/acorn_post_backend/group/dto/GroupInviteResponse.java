package yerong.acorn_post_backend.group.dto;

import yerong.acorn_post_backend.group.domain.GroupType;
public record GroupInviteResponse(
        Long groupId,
        String name,
        String description,
        String joinCode,
        GroupType type
) {}