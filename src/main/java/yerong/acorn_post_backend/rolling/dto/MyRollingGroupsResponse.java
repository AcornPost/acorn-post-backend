package yerong.acorn_post_backend.rolling.dto;

import java.util.List;

public record MyRollingGroupsResponse(
        List<MyGroupSummary> rollingPaper
) {
    public record MyGroupSummary(
            Long groupId,
            String name,
            String deadline,
            int participantCount
    ) {}
}