package yerong.acorn_post_backend.manitto.dto;

import java.time.LocalDateTime;

public record CreateManittoGroupRequest(
        String name,
        String description,
        LocalDateTime deadline
) {
}
