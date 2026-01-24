package yerong.acorn_post_backend.manitto.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yerong.acorn_post_backend.common.response.ApiResponse;
import yerong.acorn_post_backend.common.response.SuccessCode;
import yerong.acorn_post_backend.common.security.CurrentMemberIdResolver;
import yerong.acorn_post_backend.manitto.dto.LeaveGroupResponse;
import yerong.acorn_post_backend.manitto.dto.ManittoGroupSummary;
import yerong.acorn_post_backend.manitto.dto.ManittoRoomInfoResponse;
import yerong.acorn_post_backend.manitto.service.ManittoService;

@RestController
@RequestMapping("/api/manitto")
@RequiredArgsConstructor
public class ManittoApiController {
    private final ManittoService manittoService;
    private final CurrentMemberIdResolver currentMember;

    @GetMapping("/groups/{groupId}")
    public ApiResponse<ManittoRoomInfoResponse> getRoomInfo(
            @PathVariable Long groupId
    ) {
        Long memberId = currentMember.get();
        ManittoRoomInfoResponse response = manittoService.getRoomInfo(memberId, groupId);
        return ApiResponse.success(SuccessCode.MANITTO_ROOM_INFO_FETCHED, response);
    }

    @PostMapping("/groups/{groupId}/leave")
    public ApiResponse<LeaveGroupResponse> leaveGroup(
            @PathVariable Long groupId
    ) {
        Long memberId = currentMember.get();
        LeaveGroupResponse response = manittoService.leaveGroup(memberId, groupId);
        return ApiResponse.success(SuccessCode.OK, response);
    }

    @PostMapping("/groups/{groupId}/start-matching")
    public ApiResponse<Void> startMatching(
            @PathVariable Long groupId
    ) {
        Long memberId = currentMember.get();
        manittoService.startMatching(memberId, groupId);
        return ApiResponse.success(SuccessCode.MANITTO_MATCHING_STARTED, null);
    }

    @PostMapping("/groups/{groupId}/reveal")
    public ApiResponse<Void> reveal(
            @PathVariable Long groupId
    ) {
        Long memberId = currentMember.get();
        manittoService.reveal(memberId, groupId);
        return ApiResponse.success(SuccessCode.MANITTO_REVEALED, null);
    }

    @GetMapping("/groups")
    public ApiResponse<List<ManittoGroupSummary>> getMyGroups() {
        Long memberId = currentMember.get();
        List<ManittoGroupSummary> groups = manittoService.getMyGroups(memberId);
        return ApiResponse.success(SuccessCode.MANITTO_MY_GROUPS_FETCHED, groups);
    }
}
