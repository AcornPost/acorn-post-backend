package yerong.acorn_post_backend.rolling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yerong.acorn_post_backend.common.response.ApiResponse;
import yerong.acorn_post_backend.common.response.SuccessCode;
import yerong.acorn_post_backend.common.security.CurrentMemberIdResolver;
import yerong.acorn_post_backend.group.dto.MyGroupsResponse;
import yerong.acorn_post_backend.rolling.dto.GroupMembersResponse;
import yerong.acorn_post_backend.rolling.dto.MemberPaperResponse;
import yerong.acorn_post_backend.rolling.dto.MyRollingGroupsResponse;
import yerong.acorn_post_backend.rolling.dto.UpdateColorRequest;
import yerong.acorn_post_backend.rolling.dto.UpdatePositionRequest;
import yerong.acorn_post_backend.rolling.dto.WriteMessageRequest;
import yerong.acorn_post_backend.rolling.dto.WriteMessageResponse;
import yerong.acorn_post_backend.rolling.service.RollingPaperService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rolling/groups")
public class RollingPaperApiController {
    private final RollingPaperService rollingPaperService;
    private final CurrentMemberIdResolver currentMember;

    @GetMapping("/{groupId}/members")
    public ApiResponse<GroupMembersResponse> getMembers(@PathVariable Long groupId) {
        Long memberId = currentMember.get();
        GroupMembersResponse result = rollingPaperService.getGroupMembers(memberId, groupId);
        return ApiResponse.success(SuccessCode.ROLLING_GROUP_MEMBERS_FETCHED, result);
    }

    @GetMapping("/{groupId}/papers/{targetMemberId}")    public ApiResponse<MemberPaperResponse> getMemberPaper(
            @PathVariable Long groupId,
            @PathVariable Long targetMemberId
    ) {
        Long memberId = currentMember.get();
        MemberPaperResponse result = rollingPaperService.getMemberPaper(memberId, groupId, targetMemberId);
        return ApiResponse.success(SuccessCode.ROLLING_MEMBER_PAPER_FETCHED, result);
    }

    @PostMapping("/{groupId}/messages")
    public ApiResponse<WriteMessageResponse> writeMessage(
            @PathVariable Long groupId,
            @RequestBody WriteMessageRequest request
    ) {
        Long memberId = currentMember.get();
        WriteMessageResponse result = rollingPaperService.writeMessage(memberId, groupId, request);
        return ApiResponse.success(SuccessCode.ROLLING_MESSAGE_CREATED, result);
    }

    @PatchMapping("/{groupId}/messages/position")
    public ApiResponse<Void> updatePosition(
            @PathVariable Long groupId,
            @RequestBody UpdatePositionRequest request
    ) {
        Long memberId = currentMember.get();
        rollingPaperService.updateMessagePosition(memberId, groupId, request);
        return ApiResponse.success(SuccessCode.ROLLING_MESSAGE_POSITION_UPDATED, null);
    }

    @DeleteMapping("/{groupId}/messages/{messageId}")
    public ApiResponse<Void> deleteMessage(
            @PathVariable Long groupId,
            @PathVariable Long messageId
    ) {
        Long memberId = currentMember.get();
        rollingPaperService.deleteMessage(memberId, groupId, messageId);
        return ApiResponse.success(SuccessCode.ROLLING_MESSAGE_DELETED, null);
    }
    @PatchMapping("/{groupId}/messages/{messageId}")
    public ApiResponse<Void> updateMessage(
            @PathVariable Long groupId,
            @PathVariable Long messageId,
            @RequestBody WriteMessageRequest request // 내용, 색상, 모양, 글꼴 포함
    ) {
        Long memberId = currentMember.get();
        rollingPaperService.updateMessage(memberId, groupId, messageId, request);
        return ApiResponse.success(SuccessCode.ROLLING_MESSAGE_UPDATED, null);
    }
    @GetMapping("/me")
    public ApiResponse<MyRollingGroupsResponse> getMyGroups() {
        Long memberId = currentMember.get();
        MyRollingGroupsResponse result = rollingPaperService.getMyJoinedGroups(memberId);
        return ApiResponse.success(SuccessCode.ROLLING_GROUP_LIST_FETCHED, result);
    }
    @PatchMapping("/{groupId}/paper-color")
    public ApiResponse<Void> updateMyPaperColor(
            @PathVariable Long groupId,
            @RequestBody UpdateColorRequest request
    ) {
        Long memberId = currentMember.get();
        rollingPaperService.updatePaperColor(memberId, groupId, request.color());
        return ApiResponse.success(SuccessCode.ROLLING_PAPER_COLOR_UPDATED);
    }
}
