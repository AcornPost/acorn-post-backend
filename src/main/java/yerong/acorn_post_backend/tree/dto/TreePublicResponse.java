package yerong.acorn_post_backend.tree.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.tree.domain.Tree;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreePublicResponse {
    private String shareCode;
    private String title;
    private String description;
    private Boolean allowAnonymous;
    private Long totalLetters;
    private LocalDateTime createdAt;
    private String ownerNickname;
    private Boolean isOwner;

    public static TreePublicResponse from(Tree tree, Long totalLetters, Boolean isOwner) {
        return TreePublicResponse.builder()
                .shareCode(tree.getShareCode())
                .title(tree.getTitle())
                .description(tree.getDescription())
                .allowAnonymous(tree.getAllowAnonymous())
                .totalLetters(totalLetters)
                .createdAt(tree.getCreatedAt())
                .ownerNickname(tree.getOwner().getNickname())
                .isOwner(isOwner)
                .build();
    }
}