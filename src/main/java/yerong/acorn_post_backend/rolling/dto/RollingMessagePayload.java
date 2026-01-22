package yerong.acorn_post_backend.rolling.dto;

public record RollingMessagePayload(
        Long id,
        Long fromMemberId,
        String fromNickname,
        String content,
        String color,
        String shape,
        String font,
        Double positionX,
        Double positionY,
        Double rotation
) {}
