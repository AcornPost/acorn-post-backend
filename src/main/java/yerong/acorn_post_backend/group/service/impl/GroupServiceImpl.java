package yerong.acorn_post_backend.group.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.group.domain.GroupMember;
import yerong.acorn_post_backend.group.domain.GroupMemberRole;
import yerong.acorn_post_backend.group.domain.GroupType;
import yerong.acorn_post_backend.group.dto.CreateGroupRequest;
import yerong.acorn_post_backend.group.dto.CreateGroupResponse;
import yerong.acorn_post_backend.group.dto.GroupInviteResponse;
import yerong.acorn_post_backend.group.dto.GroupSummaryResponse;
import yerong.acorn_post_backend.group.dto.JoinGroupRequest;
import yerong.acorn_post_backend.group.dto.JoinGroupResponse;
import yerong.acorn_post_backend.group.dto.MyGroupsResponse;
import yerong.acorn_post_backend.group.repository.GroupMemberRepository;
import yerong.acorn_post_backend.group.repository.GroupRepository;
import yerong.acorn_post_backend.group.service.GroupService;
import yerong.acorn_post_backend.group.util.JoinCodeGenerator;
import yerong.acorn_post_backend.manitto.repository.ManittoMatchRepository;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;
    private final JoinCodeGenerator joinCodeGenerator;
    private final ManittoMatchRepository manittoMatchRepository;

    @Override
    public CreateGroupResponse createGroup(Long memberId, CreateGroupRequest request) {

        if (request.name() == null || request.name().isBlank()) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "그룹 이름은 필수입니다.");
        }

        if (request.deadline() == null || request.deadline().isBefore(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "마감 날짜가 올바르지 않습니다.");
        }

        Member host = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        String joinCode = joinCodeGenerator.generateUnique();

        Group group = Group.create(
                request.name(),
                request.description(),
                request.deadline(),
                request.type(),
                joinCode,
                host
        );

        groupRepository.save(group);
        groupMemberRepository.save(GroupMember.host(group, host, host.getNickname()));
        return new CreateGroupResponse(group.getId(), joinCode);
    }

    @Override
    public JoinGroupResponse joinGroup(Long memberId, JoinGroupRequest request) {

        Group group = groupRepository.findByJoinCode(request.joinCode())
                .orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));

        if (group.getType() == GroupType.MANITTO) {
            boolean isStarted = manittoMatchRepository.existsByGroup(group);
            if (isStarted) {
                throw new ApiException(ErrorCode.MANITTO_MATCHING_STARTED_ALREADY);
            }
        }

        if (!group.isOpen()) {
            throw new ApiException(ErrorCode.GROUP_CLOSED);
        }

        if (group.isExpired(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.GROUP_EXPIRED);
        }

        boolean alreadyJoined = groupMemberRepository.existsByGroup_IdAndMember_Id(group.getId(), memberId);

        if(alreadyJoined) {
            GroupMemberRole role = groupMemberRepository.findRoleByGroupIdAndMemberId(group.getId(), memberId);

            return new JoinGroupResponse(
                    group.getId(),
                    group.getName(),
                    group.getType(),
                    role != null ? role : GroupMemberRole.MEMBER,
                    true
            );
        }
        if (groupMemberRepository.existsByGroupIdAndGroupNickname(group.getId(), request.nickname())) {
            throw new ApiException(ErrorCode.DUPLICATE_NICKNAME);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        String nicknameToUse = (request.nickname() != null && !request.nickname().isBlank())
                ? request.nickname()
                : member.getNickname();

        groupMemberRepository.save(GroupMember.member(group, member, nicknameToUse));

        return new JoinGroupResponse(group.getId(), group.getName(), group.getType(), GroupMemberRole.MEMBER, false);
    }

    @Override
    @Transactional(readOnly = true)
    public MyGroupsResponse getMyGroups(Long memberId) {
        List<Group> groups = groupRepository.findAllByHost_IdOrderByCreatedAtDesc(memberId);

        List<GroupSummaryResponse> manitto = new ArrayList<>();
        List<GroupSummaryResponse> rolling = new ArrayList<>();

        for (Group g : groups) {
            GroupSummaryResponse dto = new GroupSummaryResponse(
                    g.getId(),
                    g.getName(),
                    g.getDescription(),
                    g.getDeadline(),
                    g.getType(),
                    g.getJoinCode(),
                    g.getCreatedAt()
            );

            if (g.getType() == GroupType.MANITTO) manitto.add(dto);
            else rolling.add(dto);
        }

        return new MyGroupsResponse(manitto, rolling);
    }

    @Override
    @Transactional
    public void updateGroupNickname(Long memberId, Long groupId, String newNickname) {
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_GROUP_MEMBER));

        boolean exists = groupMemberRepository.existsByGroupIdAndGroupNicknameAndMemberIdNot(groupId, newNickname, memberId);
        if (exists) {
            throw new ApiException(ErrorCode.DUPLICATE_NICKNAME);
        }

        groupMember.updateNickname(newNickname);
    }

    @Override
    public GroupInviteResponse getGroupByJoinCode(String joinCode) {
        Group group = groupRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new ApiException(ErrorCode.GROUP_NOT_FOUND));
        return new GroupInviteResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getJoinCode(),
                group.getType()
        );
    }
}
