package yerong.acorn_post_backend.manitto.realtime.kafka;

public record ManittoChatEvent(
        String type, // "MESSAGE" | "READ"
        Long roomId,
        Long messageId,
        Long actorMemberId
) {}