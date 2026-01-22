package yerong.acorn_post_backend.tree.dto;

public record TreeLetterPayload(
        String id,
        String writerId,
        String nickname,
        String content,
        boolean isRead,
        Double positionX,
        Double positionY,
        String createdAt
) {}
