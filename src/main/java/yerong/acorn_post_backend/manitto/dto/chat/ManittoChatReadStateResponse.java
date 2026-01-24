package yerong.acorn_post_backend.manitto.dto.chat;

public record ManittoChatReadStateResponse(
        Long roomId,
        Long readerMemberId,
        Long lastReadMessageId
) {}