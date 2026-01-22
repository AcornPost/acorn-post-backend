package yerong.acorn_post_backend.rolling.dto;

public record RollingPaperEventPayload(
        String type,
        RollingMessagePayload message
) {}
