package yerong.acorn_post_backend.rolling.realtime.event;

import yerong.acorn_post_backend.rolling.dto.MessageDetailResponse;

public record RollingEvent(
        Long groupId,
        Long toMemberId,
        RollingEventType type,
        MessageDetailResponse payload
) {}

