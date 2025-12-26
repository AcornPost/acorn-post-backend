package yerong.acorn_post_backend.rolling.dto;

import yerong.acorn_post_backend.rolling.domain.StickerShape;

public record WriteMessageRequest(
        Long toMemberId,
        String content,
        String color,
        StickerShape shape,
        String font,
        Double positionX,
        Double positionY,
        Double rotation
) {
}
