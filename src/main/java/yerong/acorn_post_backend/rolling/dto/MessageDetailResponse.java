package yerong.acorn_post_backend.rolling.dto;

import yerong.acorn_post_backend.rolling.domain.StickerShape;

public record MessageDetailResponse(
        Long id,
        Long fromMemberId,
        String fromMemberName,
        String content,
        String color,
        StickerShape shape,
        String font,
        Double x,
        Double y,
        Double rotation
) {
}
