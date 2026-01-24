package yerong.acorn_post_backend.manitto.dto.chat;

public record ManittoChatRoomResponse(
        Long roomId,
        Long groupId,
        String partnerAlias,
        String roomStatus
) {}