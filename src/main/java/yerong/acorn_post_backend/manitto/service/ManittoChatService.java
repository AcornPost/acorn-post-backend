package yerong.acorn_post_backend.manitto.service;

import yerong.acorn_post_backend.manitto.dto.chat.ManittoChatMessageListResponse;
import yerong.acorn_post_backend.manitto.dto.chat.ManittoChatRoomResponse;
import yerong.acorn_post_backend.manitto.dto.chat.SendChatMessageRequest;

public interface ManittoChatService {
    ManittoChatRoomResponse getMyRoom(Long memberId, Long groupId, String typeStr);    void sendMessage(Long memberId, Long roomId, SendChatMessageRequest request);
    ManittoChatMessageListResponse getMessages(Long memberId, Long roomId);
    void markRead(Long memberId, Long roomId, Long lastReadMessageId);
}
