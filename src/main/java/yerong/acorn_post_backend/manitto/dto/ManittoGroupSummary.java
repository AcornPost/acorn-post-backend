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
        WAITING,      // 매칭 대기
        IN_PROGRESS,  // 진행중
        COMPLETED,    // 완료
        EXPIRED       // 기간 만료
    }
}