package yerong.acorn_post_backend.group.dto;

import java.time.LocalDateTime;
import yerong.acorn_post_backend.group.domain.GroupType;

public record CreateGroupRequest(
        String name,
        String description,
        LocalDateTime deadline,
        GroupType type
) {}