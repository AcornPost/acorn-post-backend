package yerong.acorn_post_backend.manitto.dto;

import java.time.LocalDateTime;

public record ManittoMessageResponse(
        Long messageId,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {
}
