package yerong.acorn_post_backend.group.dto;

import java.util.List;

public record MyGroupsResponse(
        List<GroupSummaryResponse> manitto,
        List<GroupSummaryResponse> rollingPaper
) {}
