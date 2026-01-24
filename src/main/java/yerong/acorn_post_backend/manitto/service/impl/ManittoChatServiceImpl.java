package yerong.acorn_post_backend.manitto.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.group.repository.GroupRepository;
import yerong.acorn_post_backend.manitto.domain.ManittoMatch;
import yerong.acorn_post_backend.manitto.domain.ManittoMatchStatus;
import yerong.acorn_post_backend.manitto.domain.chat.ManittoChatMessage;
import yerong.acorn_post_backend.manitto.domain.chat.ManittoChatRoom;
import yerong.acorn_post_backend.manitto.domain.chat.ManittoChatRoomStatus;
import yerong.acorn_post_backend.manitto.dto.chat.ManittoChatMessageListResponse;
import yerong.acorn_post_backend.manitto.dto.chat.ManittoChatMessageResponse;
import yerong.acorn_post_backend.manitto.dto.chat.ManittoChatRoomResponse;
import yerong.acorn_post_backend.manitto.dto.chat.SendChatMessageRequest;
import yerong.acorn_post_backend.manitto.realtime.kafka.ManittoChatEventProducer;
import yerong.acorn_post_backend.manitto.repository.ManittoMatchRepository;
import yerong.acorn_post_backend.manitto.repository.chat.ManittoChatMessageRepository;
import yerong.acorn_post_backend.manitto.repository.chat.ManittoChatRoomRepository;
import yerong.acorn_post_backend.manitto.service.ManittoChatService;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManittoChatServiceImpl implements ManittoChatService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final ManittoMatchRepository matchRepository;
    private final ManittoChatRoomRepository roomRepository;
    private final ManittoChatMessageRepository messageRepository;
    private final ManittoChatEventProducer eventProducer;

    @Transactional
    @Override
    public ManittoChatRoomResponse getMyRoom(Long memberId, Long groupId, String type) {
        String tab = (type == null || type.isBlank()) ? "TO_TARGET" : type;

        Member me = getMember(memberId);
        Group group = getGroup(groupId);

        ManittoMatch match = "FROM_MANITTO".equals(tab)
                ? matchRepository.findByGroupAndReceiver(group, me)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_MATCH_NOT_FOUND))
                : matchRepository.findByGroupAndGiver(group, me)
                        .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_MATCH_NOT_FOUND));

        Member giver = match.getGiver();
        Member receiver = match.getReceiver();

        ManittoChatRoom room = getOrCreateRoom(group, giver, receiver);

        Member partner = "FROM_MANITTO".equals(tab) ? giver : receiver;
        boolean revealName = (match.getStatus() == ManittoMatchStatus.REVEALED);

        String alias = (room.getStatus() == ManittoChatRoomStatus.REVEALED || revealName)
                ? partner.getNickname()
                : "익명";

        return new ManittoChatRoomResponse(room.getId(), groupId, alias, room.getStatus().name());
    }


    @Override
    @Transactional
    public void sendMessage(Long memberId, Long roomId, SendChatMessageRequest request) {
        if (request == null || request.content() == null || request.content().isBlank())
            throw new ApiException(ErrorCode.MANITTO_CHAT_MESSAGE_EMPTY);

        if (request.content().length() > 500)
            throw new ApiException(ErrorCode.MANITTO_CHAT_MESSAGE_TOO_LONG);


        Member member = getMember(memberId);
        ManittoChatRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_CHAT_ROOM_NOT_FOUND));

        if (!room.contains(memberId))
            throw new ApiException(ErrorCode.MANITTO_CHAT_ROOM_FORBIDDEN);

        if (room.getStatus() != ManittoChatRoomStatus.ACTIVE)
            throw new ApiException(ErrorCode.MANITTO_CHAT_ROOM_NOT_ACTIVE);

        ManittoChatMessage saved = messageRepository.save(
                ManittoChatMessage.create(room, member, request.content()));
        eventProducer.publishMessage(saved.getId(), room.getId(), member.getId());
    }

    @Override
    public ManittoChatMessageListResponse getMessages(Long memberId, Long roomId) {
        ManittoChatRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_CHAT_ROOM_NOT_FOUND));

        if (!room.contains(memberId))
            throw new ApiException(ErrorCode.MANITTO_CHAT_ROOM_FORBIDDEN);

        List<ManittoChatMessage> last = messageRepository.findTop50ByRoomOrderByCreatedAtDesc(room);

        List<ManittoChatMessageResponse> responses = last.stream()
                .sorted((a,b)->a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(m -> new ManittoChatMessageResponse(
                        m.getId(), m.getContent(),
                        m.getSender().getId().equals(memberId),
                        m.getCreatedAt()
                )).toList();
        Long myLastRead = room.getLastReadMessageId(memberId);
        Long partnerId = room.getMemberLow().getId().equals(memberId)
                ? room.getMemberHigh().getId()
                : room.getMemberLow().getId();
        Long partnerLastRead = room.getLastReadMessageId(partnerId);

        Long myLastSent = responses.stream()
                .filter(ManittoChatMessageResponse::isMine)
                .reduce((a,b) -> b)
                .map(ManittoChatMessageResponse::messageId)
                .orElse(null);
        return new ManittoChatMessageListResponse(responses, myLastRead, partnerLastRead, myLastSent);
    }

    @Override
    @Transactional
    public void markRead(Long memberId, Long roomId, Long lastReadMessageId) {
        if (lastReadMessageId == null) throw new ApiException(ErrorCode.MANITTO_CHAT_READ_INVALID);

        ManittoChatRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_CHAT_ROOM_NOT_FOUND));

        if (!room.contains(memberId))
            throw new ApiException(ErrorCode.MANITTO_CHAT_ROOM_FORBIDDEN);

        ManittoChatMessage msg = messageRepository.findById(lastReadMessageId)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_CHAT_MESSAGE_NOT_FOUND));

        if (!msg.getRoom().getId().equals(roomId))
            throw new ApiException(ErrorCode.MANITTO_CHAT_READ_INVALID);

        Long current = room.getLastReadMessageId(memberId);
        if (current != null && lastReadMessageId <= current) return;

        roomRepository.updateLastRead(roomId, memberId, lastReadMessageId);
        eventProducer.publishRead(roomId, memberId, lastReadMessageId);
    }

    private ManittoChatRoom getOrCreateRoom(Group group, Member giver, Member receiver) {
        return roomRepository.findByGroupAndGiverAndReceiver(group, giver, receiver)
                .orElseGet(() -> roomRepository.save(ManittoChatRoom.create(group, giver, receiver)));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId).orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
    }
}
