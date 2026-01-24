package yerong.acorn_post_backend.manitto.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yerong.acorn_post_backend.common.response.ApiResponse;
import yerong.acorn_post_backend.common.response.SuccessCode;
import yerong.acorn_post_backend.common.security.CurrentMemberIdResolver;
import yerong.acorn_post_backend.manitto.dto.chat.ManittoChatMessageListResponse;
import yerong.acorn_post_backend.manitto.dto.chat.ManittoChatRoomResponse;
import yerong.acorn_post_backend.manitto.dto.chat.MarkReadRequest;
import yerong.acorn_post_backend.manitto.dto.chat.SendChatMessageRequest;
import yerong.acorn_post_backend.manitto.service.ManittoChatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manitto")
public class ManittoChatController {
    private final ManittoChatService manittoChatService;
    private final CurrentMemberIdResolver currentMemberIdResolver;

    @GetMapping("/groups/{groupId}/chat/room")
    public ApiResponse<ManittoChatRoomResponse> getMyRoom(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "TO_TARGET") String type
    ) {
        Long memberId = currentMemberIdResolver.get();
        ManittoChatRoomResponse response = manittoChatService.getMyRoom(memberId, groupId, type);
        return ApiResponse.success(SuccessCode.MANITTO_CHAT_ROOM_FETCHED, response);
    }


    @GetMapping("/chat/rooms/{roomId}/messages")
    public ApiResponse<ManittoChatMessageListResponse> getMessages(@PathVariable Long roomId) {
        Long memberId = currentMemberIdResolver.get();
        ManittoChatMessageListResponse response = manittoChatService.getMessages(memberId, roomId);
        return ApiResponse.success(SuccessCode.MANITTO_CHAT_MESSAGES_FETCHED, response);
    }

    @PostMapping("/chat/rooms/{roomId}/messages")
    public ApiResponse<Void> send(@PathVariable Long roomId, @RequestBody SendChatMessageRequest request) {
        Long memberId = currentMemberIdResolver.get();
        manittoChatService.sendMessage(memberId, roomId, request);
        return ApiResponse.success(SuccessCode.MANITTO_CHAT_MESSAGE_SENT, null);
    }

    @PostMapping("/chat/rooms/{roomId}/read")
    public ApiResponse<Void> markRead(@PathVariable Long roomId, @RequestBody MarkReadRequest request) {
        Long memberId = currentMemberIdResolver.get();
        manittoChatService.markRead(memberId, roomId, request.lastReadMessageId());
        return ApiResponse.success(SuccessCode.MANITTO_CHAT_READ_UPDATED, null);
    }
}