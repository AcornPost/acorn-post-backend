package yerong.acorn_post_backend.rolling.dto;

public record UpdatePositionRequest(
        Long messageId,
        Double x,
        Double y
) {
}
