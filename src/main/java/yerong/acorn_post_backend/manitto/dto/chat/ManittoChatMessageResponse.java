package yerong.acorn_post_backend.manitto.dto.chat;

import java.time.LocalDateTime;

public record ManittoChatMessageResponse(
        Long messageId,
        String content,
        boolean isMine,
        LocalDateTime createdAt
) {}
