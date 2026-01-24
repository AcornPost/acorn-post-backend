package yerong.acorn_post_backend.manitto.service;


import java.util.List;
import yerong.acorn_post_backend.manitto.dto.LeaveGroupResponse;
import yerong.acorn_post_backend.manitto.dto.ManittoGroupSummary;
import yerong.acorn_post_backend.manitto.dto.ManittoRoomInfoResponse;

public interface ManittoService {

    ManittoRoomInfoResponse getRoomInfo(Long memberId, Long groupId);
    LeaveGroupResponse leaveGroup(Long memberId, Long groupId);
    void startMatching(Long memberId, Long groupId);
    void reveal(Long memberId, Long groupId);
    List<ManittoGroupSummary> getMyGroups(Long memberId);
}
