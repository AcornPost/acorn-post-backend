package yerong.acorn_post_backend.rolling.dto;

import yerong.acorn_post_backend.rolling.domain.StickerShape;

public record WriteMessageResponse(
        Long messageId,
        Long fromMemberId,
        String fromMemberName,
        Long toMemberId,
        String content,
        String color,
        StickerShape shape,
        String font,
        Double positionX,
        Double positionY,
        Double rotation
) {}