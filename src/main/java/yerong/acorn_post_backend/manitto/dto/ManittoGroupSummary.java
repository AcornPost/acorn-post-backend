package yerong.acorn_post_backend.manitto.dto;

import java.time.LocalDateTime;

public record ManittoGroupSummary(
        Long groupId,
        String name,
        String description,
        LocalDateTime deadline,
        Integer participantCount,
        Integer currentRound,
        GroupStatus status,
        boolean isHost
) {
    public enum GroupStatus {
        WAITING,
        IN_PROGRESS,
        COMPLETED,
        EXPIRED
    }
}