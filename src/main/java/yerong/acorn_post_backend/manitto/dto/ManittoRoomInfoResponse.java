package yerong.acorn_post_backend.manitto.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ManittoRoomInfoResponse(
        Long groupId,
        String joinCode,
        String name,
        String description,
        LocalDateTime deadline,
        Integer participantCount,
        Integer currentRound,
        ManittoGroupSummary.GroupStatus status,
        boolean isHost,
        ParticipantInfo myInfo,
        ManittoTargetInfo myManitto,
        List<ParticipantInfo> participants,
        List<ManittoMissionInfo> missions
) {
    public record ParticipantInfo(
            Long memberId,
            String nickname,
            boolean isMe,
            String manittoNickname
    ) {}

    public record ManittoTargetInfo(
            Long memberId,
            String nickname
    ) {}

    public record ManittoMissionInfo(
            Long missionId,
            String content,
            Integer displayOrder
    ) {}
}
