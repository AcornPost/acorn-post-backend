package yerong.acorn_post_backend.rolling.dto;

import java.util.List;

public record GroupMembersResponse(
        Long groupId,
        String groupName,
        String deadline,
        String joinCode,
        List<GroupMemberSummary> members
) {
}
