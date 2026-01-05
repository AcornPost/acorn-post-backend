package yerong.acorn_post_backend.rolling.service;

import yerong.acorn_post_backend.group.dto.MyGroupsResponse;
import yerong.acorn_post_backend.rolling.dto.GroupMembersResponse;
import yerong.acorn_post_backend.rolling.dto.MemberPaperResponse;
import yerong.acorn_post_backend.rolling.dto.MyRollingGroupsResponse;
import yerong.acorn_post_backend.rolling.dto.UpdatePositionRequest;
import yerong.acorn_post_backend.rolling.dto.WriteMessageRequest;
import yerong.acorn_post_backend.rolling.dto.WriteMessageResponse;

public interface RollingPaperService {
    GroupMembersResponse getGroupMembers(Long memberId, Long groupId);
    MemberPaperResponse getMemberPaper(Long memberId, Long groupId, Long targetMemberId);
    WriteMessageResponse writeMessage(Long memberId, Long groupId, WriteMessageRequest request);
    void updateMessagePosition(Long memberId, Long groupId, UpdatePositionRequest request);
    void deleteMessage(Long memberId, Long groupId, Long messageId);
    void updateMessage(Long memberId, Long groupId, Long messageId, WriteMessageRequest request);
    MyRollingGroupsResponse getMyJoinedGroups(Long memberId);
}
