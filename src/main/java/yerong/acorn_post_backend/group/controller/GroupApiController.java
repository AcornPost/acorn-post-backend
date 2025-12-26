package yerong.acorn_post_backend.group.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yerong.acorn_post_backend.common.response.ApiResponse;
import yerong.acorn_post_backend.common.response.SuccessCode;
import yerong.acorn_post_backend.common.security.CurrentMemberIdResolver;
import yerong.acorn_post_backend.group.dto.CreateGroupRequest;
import yerong.acorn_post_backend.group.dto.CreateGroupResponse;
import yerong.acorn_post_backend.group.dto.JoinGroupRequest;
import yerong.acorn_post_backend.group.dto.JoinGroupResponse;
import yerong.acorn_post_backend.group.dto.MyGroupsResponse;
import yerong.acorn_post_backend.group.service.GroupService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupApiController {

    private final GroupService groupService;
    private final CurrentMemberIdResolver currentMember;

    @PostMapping
    public ApiResponse<CreateGroupResponse> create(@RequestBody CreateGroupRequest request) {
        Long memberId = currentMember.get();
        CreateGroupResponse result = groupService.createGroup(memberId, request);
        return ApiResponse.success(SuccessCode.GROUP_CREATED, result);
    }

    @PostMapping("/join")
    public ApiResponse<JoinGroupResponse> join(@RequestBody JoinGroupRequest request) {
        Long memberId = currentMember.get();
        JoinGroupResponse result = groupService.joinGroup(memberId, request);

        SuccessCode code = result.alreadyJoined()
                ? SuccessCode.GROUP_ALREADY_JOINED
                : SuccessCode.GROUP_JOINED;

        return ApiResponse.success(code, result);
    }
    @GetMapping("/me")
    public ApiResponse<MyGroupsResponse> myGroups() {
        Long memberId = currentMember.get();
        MyGroupsResponse result = groupService.getMyGroups(memberId);
        return ApiResponse.success(SuccessCode.GROUP_LIST_FETCHED, result);
    }
}
