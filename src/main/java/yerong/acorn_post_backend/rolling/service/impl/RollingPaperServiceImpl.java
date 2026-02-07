package yerong.acorn_post_backend.rolling.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.group.domain.GroupMember;
import yerong.acorn_post_backend.group.domain.GroupMemberStatus;
import yerong.acorn_post_backend.group.domain.GroupType;
import yerong.acorn_post_backend.group.repository.GroupMemberRepository;
import yerong.acorn_post_backend.group.repository.GroupRepository;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.member.repository.MemberRepository;
import yerong.acorn_post_backend.rolling.domain.RollingPaperMessage;
import yerong.acorn_post_backend.rolling.dto.GroupMemberSummary;
import yerong.acorn_post_backend.rolling.dto.GroupMembersResponse;
import yerong.acorn_post_backend.rolling.dto.MemberPaperResponse;
import yerong.acorn_post_backend.rolling.dto.MessageDetailResponse;
import yerong.acorn_post_backend.rolling.dto.MyRollingGroupsResponse;
import yerong.acorn_post_backend.rolling.dto.UpdatePositionRequest;
import yerong.acorn_post_backend.rolling.dto.WriteMessageRequest;
import yerong.acorn_post_backend.rolling.dto.WriteMessageResponse;
import yerong.acorn_post_backend.rolling.realtime.event.RollingEvent;
import yerong.acorn_post_backend.rolling.realtime.event.RollingEventType;
import yerong.acorn_post_backend.rolling.realtime.pubsub.RollingMessageEventBus;
import yerong.acorn_post_backend.rolling.repository.RollingPaperMessageRepository;
import yerong.acorn_post_backend.rolling.service.RollingPaperService;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RollingPaperServiceImpl implements RollingPaperService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final RollingPaperMessageRepository messageRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public GroupMembersResponse getGroupMembers(Long memberId, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));

        if (group.getType() != GroupType.ROLLING_PAPER) {
            throw new ApiException(ErrorCode.ROLLING_GROUP_ONLY);
        }

        if (!groupMemberRepository.existsByGroup_IdAndMember_Id(groupId, memberId)) {
            throw new ApiException(ErrorCode.ROLLING_GROUP_MEMBER_ONLY);
        }

        List<GroupMember> groupMembers = groupMemberRepository
                .findAllByGroup_IdAndStatus(groupId, GroupMemberStatus.ACTIVE);
        List<Long> writtenToIds = messageRepository
                .findToMemberIdsByGroupAndFromMember(groupId, memberId);

        List<GroupMemberSummary> members = groupMembers.stream()
                .map(gm -> new GroupMemberSummary(
                        gm.getMember().getId(),
                        gm.getGroupNickname(),
                        writtenToIds.contains(gm.getMember().getId())
                ))
                .collect(Collectors.toList());

        String deadline = group.getDeadline()
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        return new GroupMembersResponse(groupId, group.getName(), deadline, group.getJoinCode(), members);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberPaperResponse getMemberPaper(Long member, Long groupId, Long targetMemberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));


        if (group.getType() != GroupType.ROLLING_PAPER) {
            throw new ApiException(ErrorCode.ROLLING_GROUP_ONLY);
        }

        if (!groupMemberRepository.existsByGroup_IdAndMember_Id(groupId, member)) {
            throw new ApiException(ErrorCode.ROLLING_GROUP_MEMBER_ONLY);
        }

        if (!groupMemberRepository.existsByGroup_IdAndMember_Id(groupId, targetMemberId)) {
            throw new ApiException(ErrorCode.ROLLING_GROUP_MEMBER_ONLY);
        }

        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        List<RollingPaperMessage> messages = messageRepository
                .findAllByGroup_IdAndToMember_Id(groupId, targetMemberId);
        List<MessageDetailResponse> messageDetails = messages.stream()
                .map(m -> new MessageDetailResponse(
                        m.getId(),
                        m.getFromMember().getId(),
                        m.getFromMember().getNickname(),
                        m.getContent(),
                        m.getColor(),
                        m.getShape(),
                        m.getFont(),
                        m.getPositionX(),
                        m.getPositionY(),
                        m.getRotation()
                ))
                .collect(Collectors.toList());
        String deadline = group.getDeadline()
                .format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        GroupMember targetGm = groupMemberRepository.findByGroup_IdAndMember_Id(groupId, targetMemberId)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLLING_GROUP_MEMBER_ONLY));

        return new MemberPaperResponse(
                targetMemberId,
                targetMember.getNickname(),
                deadline,
                targetGm.getPaperBgColor(),
                messageDetails
        );
    }

    @Override
    public WriteMessageResponse writeMessage(Long memberId, Long groupId, WriteMessageRequest request) {
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "메시지 내용은 필수입니다.");
        }
        if (request.content().length() > 100) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "메시지는 100자 이하여야 합니다.");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));

        if (group.getType() != GroupType.ROLLING_PAPER) {
            throw new ApiException(ErrorCode.ROLLING_GROUP_ONLY);
        }

        if (!groupMemberRepository.existsByGroup_IdAndMember_Id(groupId, memberId)) {
            throw new ApiException(ErrorCode.ROLLING_GROUP_MEMBER_ONLY);
        }

        Member fromMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        Member toMember = memberRepository.findById(request.toMemberId())
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        if (!groupMemberRepository.existsByGroup_IdAndMember_Id(groupId, request.toMemberId())) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "받는 사람이 그룹 멤버가 아닙니다.");
        }

        if (messageRepository.existsByGroup_IdAndFromMember_IdAndToMember_Id(
                groupId, memberId, request.toMemberId())) {
            throw new ApiException(ErrorCode.ROLLING_MESSAGE_ALREADY_EXISTS);
        }

        RollingPaperMessage message = RollingPaperMessage.create(
                group,
                fromMember,
                toMember,
                request.content(),
                request.color(),
                request.shape(),
                request.font(),
                request.positionX(),
                request.positionY(),
                request.rotation()
        );

        messageRepository.save(message);
        publishEvent(groupId, request.toMemberId(), RollingEventType.ADDED, toDetail(message));

        return new WriteMessageResponse(
                message.getId(),
                fromMember.getId(),
                fromMember.getNickname(),
                toMember.getId(),
                message.getContent(),
                message.getColor(),
                message.getShape(),
                message.getFont(),
                message.getPositionX(),
                message.getPositionY(),
                message.getRotation()
        );
    }

    @Override
    public  void updateMessagePosition(Long memberId, Long groupId, UpdatePositionRequest request) {
        RollingPaperMessage message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new ApiException(ErrorCode.ROLLING_MESSAGE_NOT_FOUND));

        if (!message.getFromMember().getId().equals(memberId)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "본인이 작성한 메시지만 수정할 수 있습니다.");
        }

        if (!message.getGroup().getId().equals(groupId)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "잘못된 그룹입니다.");
        }

        message.updatePosition(request.x(), request.y());
        publishEvent(groupId, message.getToMember().getId(), RollingEventType.MOVED, toDetail(message));

    }
    @Override
    public void deleteMessage(Long memberId, Long groupId, Long messageId) {
        RollingPaperMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLLING_MESSAGE_NOT_FOUND));

        if (!message.getFromMember().getId().equals(memberId)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "본인이 작성한 메시지만 삭제할 수 있습니다.");
        }

        if (!message.getGroup().getId().equals(groupId)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "잘못된 그룹입니다.");
        }
        Long toMemberId = message.getToMember().getId();
        Long fromMemberId = message.getFromMember().getId();
        String fromNickname = message.getFromMember().getNickname();

        messageRepository.delete(message);

        MessageDetailResponse deletedPayload = new MessageDetailResponse(
                messageId,
                fromMemberId,
                fromNickname,
                null, null, null, null, null, null, null
        );

        publishEvent(groupId, toMemberId, RollingEventType.DELETED, deletedPayload);
    }
    @Override
    public void updateMessage(Long memberId, Long groupId, Long messageId, WriteMessageRequest request) {
        RollingPaperMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLLING_MESSAGE_NOT_FOUND));

        if (!message.getFromMember().getId().equals(memberId)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "본인이 작성한 메시지만 수정할 수 있습니다.");
        }

        if (!message.getGroup().getId().equals(groupId)) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "잘못된 그룹입니다.");
        }

        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "메시지 내용은 필수입니다.");
        }

        message.updateContent(
                request.content(),
                request.color(),
                request.shape(),
                request.font()
        );
        publishEvent(groupId, message.getToMember().getId(), RollingEventType.UPDATED, toDetail(message));
    }

    @Override
    @Transactional(readOnly = true)
    public MyRollingGroupsResponse getMyJoinedGroups(Long memberId) {
        List<GroupMember> participations = groupMemberRepository
                .findAllByMember_IdAndStatus(memberId, GroupMemberStatus.ACTIVE);

        List<MyRollingGroupsResponse.MyGroupSummary> rollingPaperGroups = participations.stream()
                .map(GroupMember::getGroup)
                .filter(group -> group.getType() == GroupType.ROLLING_PAPER)
                .map(group -> {
                    int count = groupMemberRepository.countByGroup_IdAndStatus(group.getId(), GroupMemberStatus.ACTIVE);

                    return new MyRollingGroupsResponse.MyGroupSummary(
                            group.getId(),
                            group.getName(),
                            group.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            count
                    );
                })
                .collect(Collectors.toList());

        return new MyRollingGroupsResponse(rollingPaperGroups);
    }

    @Override
    public void updatePaperColor(Long memberId, Long groupId, String color) {
        GroupMember groupMember = groupMemberRepository.findByGroup_IdAndMember_Id(groupId, memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLLING_GROUP_MEMBER_ONLY));
        groupMember.updatePaperBgColor(color);
    }

    private void publishEvent(Long groupId, Long toMemberId, RollingEventType type, MessageDetailResponse payload) {
        RollingEvent event = new RollingEvent(groupId, toMemberId, type, payload);

        log.info("[QUEUE_EVENT] type={}, groupId={}, toMemberId={}, messageId={}",
                type, groupId, toMemberId, payload != null ? payload.id() : null);
        applicationEventPublisher.publishEvent(event);
    }


    private MessageDetailResponse toDetail(RollingPaperMessage m) {
        return new MessageDetailResponse(
                m.getId(),
                m.getFromMember().getId(),
                m.getFromMember().getNickname(),
                m.getContent(),
                m.getColor(),
                m.getShape(),
                m.getFont(),
                m.getPositionX(),
                m.getPositionY(),
                m.getRotation()
        );
    }
}
