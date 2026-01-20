package yerong.acorn_post_backend.tree.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.tree.domain.Letter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterResponse {
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private Position position;
    private Boolean isRead;
    private Boolean isMine;

    public static LetterResponse from(Letter letter, Long currentMemberId) {
        return LetterResponse.builder()
                .id(letter.getId())
                .nickname(letter.getNickname())
                .content(letter.getContent())
                .createdAt(letter.getCreatedAt())
                .position(new Position(letter.getPositionX(), letter.getPositionY()))
                .isRead(letter.getIsRead())
                .isMine(currentMemberId != null && letter.getWriter() != null &&
                        letter.getWriter().getId().equals(currentMemberId))
                .build();
    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Position {
        private Double x;
        private Double y;
    }
}
