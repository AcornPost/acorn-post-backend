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
import yerong.acorn_post_backend.manitto.domain.ManittoMessage;
import yerong.acorn_post_backend.manitto.domain.ManittoMission;
import yerong.acorn_post_backend.manitto.domain.ManittoRound;
import yerong.acorn_post_backend.manitto.domain.ManittoRoundStatus;
import yerong.acorn_post_backend.manitto.dto.LeaveGroupResponse;
import yerong.acorn_post_backend.manitto.dto.ManittoGroupSummary;
import yerong.acorn_post_backend.manitto.dto.ManittoMessageListResponse;
import yerong.acorn_post_backend.manitto.dto.ManittoMessageResponse;
import yerong.acorn_post_backend.manitto.dto.ManittoRoomInfoResponse;
import yerong.acorn_post_backend.manitto.dto.SendManittoMessageRequest;
import yerong.acorn_post_backend.manitto.repository.ManittoMatchRepository;
import yerong.acorn_post_backend.manitto.repository.ManittoMessageRepository;
import yerong.acorn_post_backend.manitto.repository.ManittoMissionRepository;
import yerong.acorn_post_backend.manitto.repository.ManittoRoundRepository;
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
    private final ManittoRoundRepository roundRepository;
    private final ManittoMatchRepository matchRepository;
    private final ManittoMessageRepository messageRepository;
    private final ManittoMissionRepository missionRepository;

    private static final List<String> MISSION_POOL = List.of(
            "상대방에게 따뜻한 음료 몰래 선물하기", "상대방의 책상 위에 짧은 응원 메시지 남기기",
            "자연스럽게 상대방의 장점 3가지 칭찬하기", "상대방이 좋아하는 간식 전해주기",
            "하루에 한 번 이상 상대방과 눈 마주치며 인사하기", "상대방의 말에 크게 공감해주거나 리액션해주기",
            "상대방의 옷차림이나 헤어스타일 변화 알아봐주기", "상대방의 평소에 자주 쓰는 말 따라 해보기",
            "상대방에게 힘이 되는 노래 추천해주기", "상대방이 지나가는 길에 작은 꽃 한 송이 놓아두기",
            "상대방이 하는 일에 대해 '최고야!'라고 말해주기", "상대방의 SNS나 커뮤니티 글에 정성스러운 댓글 달기",
            "상대방과 1분 이상 진솔하게 대화 나누기", "상대방이 흘린 물건이나 쓰레기 대신 치워주기",
            "상대방에게 예쁜 필기구 선물하기", "상대방이 피곤해 보일 때 비타민 챙겨주기",
            "상대방의 별명 다정하게 불러주기", "상대방이 좋아하는 연예인이나 관심사 물어봐주기",
            "상대방의 농담에 누구보다 크게 웃어주기", "상대방이 도움을 필요로 할 때 먼저 손 내밀기",
            "상대방에게 '오늘 정말 수고했어'라고 말하기", "상대방이 좋아하는 색깔 아이템 착용하고 가기",
            "상대방의 걸음걸이 속도 맞춰서 같이 걷기", "상대방에게 예쁜 스티커나 마스킹 테이프 나눔하기",
            "상대방이 하는 고민 진지하게 들어주기", "상대방에게 '너랑 있으면 즐거워'라고 표현하기",
            "상대방의 자리에 포스트잇으로 익명 편지 쓰기", "상대방이 물 마실 때 '물 많이 마셔!'라고 챙겨주기",
            "상대방이 좋아하는 음식이나 맛집 물어보기", "상대방의 생일이나 기념일 기억해두었다 언급하기",
            "상대방에게 따뜻한 핸드크림 빌려주기", "상대방이 하는 습관 관찰해보기",
            "상대방의 기분이 좋아 보이게 만드는 문장 말해주기", "상대방이 좋아하는 계절 알아내기",
            "상대방에게 직접 그린 귀여운 캐릭터 선물하기", "상대방의 어깨 가볍게 토닥여주기",
            "상대방이 하는 프로젝트나 공부 응원해주기", "상대방과 함께 사진 찍기 시도해보기",
            "상대방에게 '항상 응원해'라고 문자 보내기", "상대방이 좋아하는 가수의 노래 같이 들어보기",
            "상대방에게 책 한 구절 공유해주기", "상대방의 책상 위 물건 정돈해 주기",
            "상대방에게 예쁜 엽서 써주기", "상대방이 자주 마시는 음료 종류 알아내기",
            "상대방에게 행운의 상징 전해주기", "상대방을 누구보다 크게 웃게 만들기",
            "상대방과 하이파이브 하기", "상대방에게 '네 덕분에 힘이 나'라고 말하기",
            "상대방의 MBTI 알아내고 공감해주기", "상대방에게 귀여운 종이접기 선물하기"
    );

    @Override
    public ManittoRoomInfoResponse getRoomInfo(Long memberId, Long groupId) {
        Member member = getMember(memberId);
        Group group = getGroup(groupId);
        validateGroupType(group);

        // 마니또 그룹 멤버인지 검증
        groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_GROUP_MEMBER_ONLY));

        ManittoRound latestRound = roundRepository
                .findTopByGroupOrderByRoundNumberDesc(group)
                .orElse(null);

        Integer currentRound = latestRound != null ? latestRound.getRoundNumber() : 0;
        ManittoGroupSummary.GroupStatus status = resolveGroupStatus(group, latestRound);

        List<GroupMember> members = groupMemberRepository
                .findAllByGroup_IdAndStatus(groupId, GroupMemberStatus.ACTIVE);

        List<ManittoRoomInfoResponse.ParticipantInfo> participants = members.stream()
                .map(m -> {
                    String manittoNickname = null;

                    if (status == ManittoGroupSummary.GroupStatus.COMPLETED || status == ManittoGroupSummary.GroupStatus.EXPIRED) {
                        manittoNickname = matchRepository
                                .findByGroupAndRoundNumberAndGiver(group, currentRound, m.getMember())
                                .map(match -> match.getReceiver().getNickname())
                                .orElse(null);
                    }

                    return new ManittoRoomInfoResponse.ParticipantInfo(
                            m.getMember().getId(),
                            m.getMember().getNickname(),
                            m.getMember().getId().equals(memberId),
                            manittoNickname
                    );
                })
                .toList();

        ManittoRoomInfoResponse.ManittoTargetInfo myTarget = null;
        List<ManittoMission> missions = List.of();

        if (latestRound != null) {
            myTarget = matchRepository
                    .findByGroupAndRoundNumberAndGiver(group, latestRound.getRoundNumber(), member)
                    .map(match -> new ManittoRoomInfoResponse.ManittoTargetInfo(
                            match.getReceiver().getId(),
                            match.getReceiver().getNickname()
                    ))
                    .orElse(null);

            missions = missionRepository
                    .findByGroupAndRoundNumberOrderByDisplayOrderAsc(group, latestRound.getRoundNumber());
        }

        List<ManittoRoomInfoResponse.ManittoMissionInfo> missionDtos = missions.stream()
                .map(m -> new ManittoRoomInfoResponse.ManittoMissionInfo(
                        m.getId(),
                        m.getContent(),
                        m.getDisplayOrder()
                ))
                .toList();

        return new ManittoRoomInfoResponse(
                group.getId(),
                group.getJoinCode(),
                group.getName(),
                group.getDescription(),
                group.getDeadline(),
                members.size(),
                currentRound,
                status,
                isHost(group, memberId),
                new ManittoRoomInfoResponse.ParticipantInfo(
                        member.getId(),
                        member.getNickname(),
                        true,
                        null
                ),
                myTarget,
                participants,
                missionDtos
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
        validateGroupType(group);
        validateHost(group, memberId);

        if (roundRepository.existsByGroupAndStatus(group, ManittoRoundStatus.IN_PROGRESS)) {
            throw new ApiException(ErrorCode.MANITTO_ROUND_IN_PROGRESS_EXISTS);
        }

        List<GroupMember> members = groupMemberRepository
                .findAllByGroup_IdAndStatus(groupId, GroupMemberStatus.ACTIVE);

        if (members.size() < 2) {
            throw new ApiException(ErrorCode.MANITTO_MIN_PARTICIPANTS_NOT_MET);
        }

        int nextRoundNumber = roundRepository
                .findTopByGroupOrderByRoundNumberDesc(group)
                .map(ManittoRound::getRoundNumber)
                .orElse(0) + 1;

        ManittoRound round = ManittoRound.create(group, nextRoundNumber);
        roundRepository.save(round);

        createRandomMissions(group, nextRoundNumber);
        createRandomMatches(group, members, nextRoundNumber);
    }

    @Override
    @Transactional
    public void reveal(Long memberId, Long groupId) {
        Group group = getGroup(groupId);
        validateGroupType(group);
        validateHost(group, memberId);

        ManittoRound round = roundRepository
                .findByGroupAndStatus(group, ManittoRoundStatus.IN_PROGRESS)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_ROUND_NOT_FOUND));

        List<ManittoMatch> matches =
                matchRepository.findByGroupAndRoundNumber(group, round.getRoundNumber());

        matches.forEach(ManittoMatch::reveal);
        round.complete();
    }

    @Override
    @Transactional
    public void restart(Long memberId, Long groupId) {
        Group group = getGroup(groupId);
        validateGroupType(group);
        validateHost(group, memberId);

        ManittoRound latest = roundRepository
                .findTopByGroupOrderByRoundNumberDesc(group)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_PREVIOUS_ROUND_NOT_FOUND));

        if (!latest.isCompleted()) {
            throw new ApiException(ErrorCode.MANITTO_ROUND_NOT_COMPLETED);
        }

        List<GroupMember> members = groupMemberRepository
                .findAllByGroup_IdAndStatus(groupId, GroupMemberStatus.ACTIVE);

        if (members.size() < 2) {
            throw new ApiException(ErrorCode.MANITTO_MIN_PARTICIPANTS_NOT_MET);
        }

        int nextRoundNumber = latest.getRoundNumber() + 1;
        ManittoRound round = ManittoRound.create(group, nextRoundNumber);
        roundRepository.save(round);

        createRandomMatches(group, members, nextRoundNumber);
    }

    @Override
    @Transactional
    public void sendMessage(Long memberId, Long groupId, SendManittoMessageRequest request) {
        Member from = getMember(memberId);
        Group group = getGroup(groupId);
        validateGroupType(group);

        ManittoRound round = roundRepository
                .findByGroupAndStatus(group, ManittoRoundStatus.IN_PROGRESS)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_ROUND_NOT_FOUND));

        ManittoMatch myMatch = matchRepository
                .findByGroupAndRoundNumberAndGiver(group, round.getRoundNumber(), from)
                .orElseThrow(() -> new ApiException(ErrorCode.MANITTO_MATCH_NOT_FOUND));

        ManittoMessage message = ManittoMessage.create(
                group,
                from,
                myMatch.getReceiver(),
                request.content(),
                round.getRoundNumber()
        );

        messageRepository.save(message);
    }

    @Override
    @Transactional
    public ManittoMessageListResponse getMyMessages(Long memberId, Long groupId) {
        Member me = getMember(memberId);
        Group group = getGroup(groupId);
        validateGroupType(group);

        ManittoRound round = roundRepository
                .findTopByGroupOrderByRoundNumberDesc(group)
                .orElse(null);

        if (round == null) {
            return new ManittoMessageListResponse(List.of());
        }

        List<ManittoMessage> messages = messageRepository
                .findByGroupAndRoundNumberAndToMemberOrderByCreatedAtAsc(
                        group, round.getRoundNumber(), me
                );

        messages.forEach(ManittoMessage::markAsRead);

        List<ManittoMessageResponse> dtos = messages.stream()
                .map(m -> new ManittoMessageResponse(
                        m.getId(),
                        m.getContent(),
                        m.getIsRead(),
                        m.getCreatedAt()
                ))
                .toList();

        return new ManittoMessageListResponse(dtos);
    }

    // ===== private 유틸 메서드 =====

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

    private ManittoGroupSummary.GroupStatus resolveGroupStatus(Group group, ManittoRound latestRound) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime revealTime = null;

        if (group.getDeadline() != null) {
            revealTime = group.getDeadline().toLocalDate().atTime(12, 0);
        }

        if (revealTime != null && now.isAfter(revealTime)) {
            return ManittoGroupSummary.GroupStatus.EXPIRED;
        }

        if (latestRound == null) {
            return ManittoGroupSummary.GroupStatus.WAITING;
        }

        if (latestRound.isInProgress()) {
            return ManittoGroupSummary.GroupStatus.IN_PROGRESS;
        }

        return ManittoGroupSummary.GroupStatus.COMPLETED;
    }

    private void createRandomMatches(
            Group group,
            List<GroupMember> members,
            int roundNumber
    ) {
        List<Member> participants = members.stream()
                .map(GroupMember::getMember)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(participants);

        int size = participants.size();
        for (int i = 0; i < size; i++) {
            Member giver = participants.get(i);
            Member receiver = participants.get((i + 1) % size); // 사이클 형태

            ManittoMatch match = ManittoMatch.create(group, giver, receiver, roundNumber);
            matchRepository.save(match);
        }
    }

    private void createRandomMissions(Group group, int roundNumber) {
        List<String> shuffledMissions = new ArrayList<>(MISSION_POOL);
        Collections.shuffle(shuffledMissions);

        // 섞인 미션 중 상위 3개를 뽑아서 저장
        for (int i = 0; i < 3; i++) {
            ManittoMission mission = ManittoMission.create(
                    group,
                    shuffledMissions.get(i),
                    roundNumber,
                    i + 1 // display_order
            );
            missionRepository.save(mission);
        }
    }

    @Override
    public List<ManittoGroupSummary> getMyGroups(Long memberId) {
        Member member = getMember(memberId);

        List<GroupMember> myMemberships = groupMemberRepository
                .findAllByMember_IdAndStatus(memberId, GroupMemberStatus.ACTIVE);

        return myMemberships.stream()
                .map(GroupMember::getGroup)
                .filter(group -> group.getType() == GroupType.MANITTO) // 마니또 그룹만
                .map(group -> {
                    ManittoRound latestRound = roundRepository
                            .findTopByGroupOrderByRoundNumberDesc(group)
                            .orElse(null);

                    ManittoGroupSummary.GroupStatus status = resolveGroupStatus(group, latestRound);
                    int currentRound = latestRound != null ? latestRound.getRoundNumber() : 0;

                    int memberCount = groupMemberRepository
                            .findAllByGroup_IdAndStatus(group.getId(), GroupMemberStatus.ACTIVE)
                            .size();

                    return new ManittoGroupSummary(
                            group.getId(),
                            group.getName(),
                            group.getDescription(),
                            group.getDeadline(),
                            memberCount,
                            currentRound,
                            status,
                            isHost(group, memberId)
                    );
                })
                .toList();
    }
}