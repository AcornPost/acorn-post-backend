package yerong.acorn_post_backend.tree.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreeInfoResponse {
    private Long totalLetters;
    private Long unreadCount;

    public static TreeInfoResponse of(long totalLetters, long unreadCount) {
        return TreeInfoResponse.builder()
                .totalLetters(totalLetters)
                .unreadCount(unreadCount)
                .build();
    }
}
