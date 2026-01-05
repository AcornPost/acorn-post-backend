package yerong.acorn_post_backend.manitto.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.group.domain.GroupMember;
import yerong.acorn_post_backend.group.domain.GroupMemberRole;
import yerong.acorn_post_backend.group.domain.GroupMemberStatus;
import yerong.acorn_post_backend.group.domain.GroupType;
import yerong.acorn_post_backend.group.repository.GroupMemberRepository;
import yerong.acorn_post_backend.group.repository.GroupRepository;
import yerong.acorn_post_backend.manitto.domain.ManittoMatch;
import yerong.acorn_post_backend.manitto.domain.ManittoMatchStatus;
import yerong.acorn_post_backend.manitto.domain.ManittoMessage;
import yerong.acorn_post_backend.manitto.domain.MissionType;
import yerong.acorn_post_backend.manitto.dto.LeaveGroupResponse;
import yerong.acorn_post_backend.manitto.dto.ManittoGroupSummary;
import yerong.acorn_post_backend.manitto.dto.ManittoMessageListResponse;
import yerong.acorn_post_backend.manitto.dto.ManittoMessageResponse;
import yerong.acorn_post_backend.manitto.dto.ManittoRoomInfoResponse;
import yerong.acorn_post_backend.manitto.dto.SendManittoMessageRequest;
import yerong.acorn_post_backend.manitto.repository.ManittoMatchRepository;
import yerong.acorn_post_backend.manitto.repository.ManittoMessageRepository;
import yerong.acorn_post_backend.manitto.service.ManittoService;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManittoServiceImpl implements ManittoService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final ManittoMatchRepository matchRepository;
    private final ManittoMessageRepository messageRepository;


    @Override
    public ManittoRoomInfoResponse getRoomInfo(Long memberId, Long groupId) {
        Member member = getMember(memberId);
        Group group = getGroup(groupId);
        validateGroupType(group);

        List<ManittoMatch> matches = matchRepository.findByGroup(group);
        ManittoGroupSummary.GroupStatus status = resolveGroupStatus(group, matches);

        List<GroupMember> members = groupMemberRepository
                .findAllByGroup_IdAndStatus(groupId, GroupMemberStatus.ACTIVE);

        List<ManittoRoomInfoResponse.ParticipantInfo> participants = members.stream()
                .map(m -> {
                    String manittoNickname = null;
                    if (status == ManittoGroupSummary.GroupStatus.COMPLETED || status == ManittoGroupSummary.GroupStatus.EXPIRED) {
                        manittoNickname = matchRepository.findByGroupAndGiver(group, m.getMember())
                                .map(match -> match.getReceiver().getNickname())
                                .orElse(null);
                    }
                    return new ManittoRoomInfoResponse.ParticipantInfo(
                            m.getMember().getId(), m.getMember().getNickname(),
                            m.getMember().getId().equals(memberId), manittoNickname
                    );
                }).toList();

        ManittoRoomInfoResponse.ManittoTargetInfo myTarget = null;
        String myMissionContent = null;

        ManittoMatch myMatch = matchRepository.findByGroupAndGiver(group, member).orElse(null);
        if (myMatch != null) {
            myTarget = new ManittoRoomInfoResponse.ManittoTargetInfo(
                    myMatch.getReceiver().getId(), myMatch.getReceiver().getNickname());
            myMissionContent = myMatch.getMission().getDescription();
        }

        return new ManittoRoomInfoResponse(
                group.getId(), group.getJoinCode(), group.getName(), group.getDescription(),
                group.getDeadline(), members.size(),
                status, isHost(group, memberId),
                new ManittoRoomInfoResponse.ParticipantInfo(member.getId(), member.getNickname(), true, null),
                myTarget, participants, myMissionContent
        );
    }

    @Override
    @Transactional
    public LeaveGroupResponse leaveGroup(Long memberId, Long groupId) {
        Member member = getMember(memberId);
        Group group = getGroup(groupId);
        validateGroupType(group);

        GroupMember groupMember = groupMemberRepository
                .findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_GROUP_MEMBER_ONLY));

        if (groupMember.getRole() == GroupMemberRole.HOST) {
            throw new ApiException(ErrorCode.MANITTO_HOST_CANNOT_LEAVE);
        }

        groupMember.leave();

        return new LeaveGroupResponse("그룹을 나갔습니다.");
    }

    @Override
    @Transactional
    public void startMatching(Long memberId, Long groupId) {
        Group group = getGroup(groupId);
        validateHost(group, memberId);

        if (matchRepository.existsByGroup(group)) {
            throw new ApiException(ErrorCode.MANITTO_MATCHING_STARTED_ALREADY);
        }

        List<GroupMember> activeMembers = groupMemberRepository
                .findAllByGroup_IdAndStatus(groupId, GroupMemberStatus.ACTIVE);

        if (activeMembers.size() < 2) {
            throw new ApiException(ErrorCode.MANITTO_MIN_PARTICIPANTS_NOT_MET);
        }

        List<Member> participants = new ArrayList<>(activeMembers.stream().map(GroupMember::getMember).toList());
        Collections.shuffle(participants);

        int size = participants.size();
        for (int i = 0; i < size; i++) {
            Member giver = participants.get(i);
            Member receiver = participants.get((i + 1) % size);

            MissionType randomMission = MissionType.getRandomMissions(1).get(0);

            ManittoMatch match = ManittoMatch.create(group, giver, receiver, randomMission);
            matchRepository.save(match);
        }
    }

    @Override
    @Transactional
    public void reveal(Long memberId, Long groupId) {
        Group group = getGroup(groupId);
        validateHost(group, memberId);

        List<ManittoMatch> matches = matchRepository.findByGroup(group);
        if (matches.isEmpty()) throw new ApiException(ErrorCode.MANITTO_MATCH_NOT_FOUND);

        matches.forEach(ManittoMatch::reveal);
    }

    @Override
    @Transactional
    public void sendMessage(Long memberId, Long groupId, SendManittoMessageRequest request) {
        Member from = getMember(memberId);
        Group group = getGroup(groupId);

        ManittoMatch myMatch = matchRepository.findByGroupAndGiver(group, from)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_MATCH_NOT_FOUND));

        if (myMatch.getStatus() != ManittoMatchStatus.ACTIVE) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "활동이 종료되어 쪽지를 보낼 수 없습니다.");
        }

        ManittoMessage message = ManittoMessage.create(
                group, from, myMatch.getReceiver(), request.content(), 1);
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public ManittoMessageListResponse getMyMessages(Long memberId, Long groupId) {
        Member me = getMember(memberId);
        Group group = getGroup(groupId);

        List<ManittoMessage> messages = messageRepository
                .findByGroupAndToMemberOrderByCreatedAtAsc(group, me);

        messages.forEach(ManittoMessage::markAsRead);

        return new ManittoMessageListResponse(messages.stream()
                .map(m -> new ManittoMessageResponse(m.getId(), m.getContent(), m.getIsRead(), m.getCreatedAt()))
                .toList());
    }

    @Override
    public List<ManittoGroupSummary> getMyGroups(Long memberId) {
        return groupMemberRepository.findAllByMember_IdAndStatus(memberId, GroupMemberStatus.ACTIVE)
                .stream()
                .map(GroupMember::getGroup)
                .filter(group -> group.getType() == GroupType.MANITTO)
                .map(group -> {
                    List<ManittoMatch> matches = matchRepository.findByGroup(group);
                    return new ManittoGroupSummary(
                            group.getId(), group.getName(), group.getDescription(),
                            group.getDeadline(),
                            groupMemberRepository.countByGroupIdAndStatus(group.getId(), GroupMemberStatus.ACTIVE),
                            1, resolveGroupStatus(group, matches), isHost(group, memberId)
                    );
                }).toList();
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
    }

    private void validateGroupType(Group group) {
        if (group.getType() != GroupType.MANITTO) {
            throw new ApiException(ErrorCode.MANITTO_GROUP_ONLY);
        }
    }

    private boolean isHost(Group group, Long memberId) {
        return group.getHost() != null && group.getHost().getId().equals(memberId);
    }

    private void validateHost(Group group, Long memberId) {
        if (!isHost(group, memberId)) {
            throw new ApiException(ErrorCode.MANITTO_HOST_ONLY);
        }
    }

    private ManittoGroupSummary.GroupStatus resolveGroupStatus(Group group, List<ManittoMatch> matches) {
        if (matches.isEmpty()) return ManittoGroupSummary.GroupStatus.WAITING;

        // 하나라도 REVEALED 상태면 전체를 완료로 판단
        boolean isRevealed = matches.get(0).getStatus() == ManittoMatchStatus.REVEALED;
        if (isRevealed) return ManittoGroupSummary.GroupStatus.COMPLETED;

        if (group.getDeadline() != null && LocalDateTime.now().isAfter(group.getDeadline())) {
            return ManittoGroupSummary.GroupStatus.EXPIRED;
        }
        return ManittoGroupSummary.GroupStatus.IN_PROGRESS;
    }

}