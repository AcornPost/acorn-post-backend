package yerong.acorn_post_backend.tree.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.tree.domain.Letter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterRequest {
    private String nickname;
    private String content;

    public Letter toEntity(double positionX, double positionY) {
        return Letter.builder()
                .nickname(nickname)
                .content(content)
                .positionX(positionX)
                .positionY(positionY)
                .isMine(true)
                .isRead(true)
                .build();
    }
}
