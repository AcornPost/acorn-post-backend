package yerong.acorn_post_backend.manitto.dto;

import java.util.List;

public record ManittoGroupListResponse (
        List<ManittoGroupSummary> groups
) {
}
