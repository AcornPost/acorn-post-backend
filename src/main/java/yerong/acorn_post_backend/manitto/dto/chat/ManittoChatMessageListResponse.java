package yerong.acorn_post_backend.manitto.dto.chat;

import java.util.List;

public record ManittoChatMessageListResponse(
        List<ManittoChatMessageResponse> messages,
        Long myLastReadMessageId,
        Long partnerLastReadMessageId,
        Long myLastSentMessageId
) {}
