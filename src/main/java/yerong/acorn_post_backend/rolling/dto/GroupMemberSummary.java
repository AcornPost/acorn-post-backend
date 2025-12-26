package yerong.acorn_post_backend.rolling.dto;

public record GroupMemberSummary(
        Long memberId,
        String memberName,
        boolean hasWritten
) {}
