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
public class TreeResponse {
    private Long id;
    private String shareCode;
    private String title;
    private String description;
    private Boolean allowAnonymous;
    private Boolean requireApproval;
    private Long totalLetters;
    private Long unreadCount;
    private Long pendingCount;
    private LocalDateTime createdAt;
    private String ownerNickname;

    public static TreeResponse from(Tree tree, Long totalLetters, Long unreadCount, Long pendingCount) {
        return TreeResponse.builder()
                .id(tree.getId())
                .shareCode(tree.getShareCode())
                .title(tree.getTitle())
                .description(tree.getDescription())
                .allowAnonymous(tree.getAllowAnonymous())
                .requireApproval(tree.getRequireApproval())
                .totalLetters(totalLetters)
                .unreadCount(unreadCount)
                .pendingCount(pendingCount)
                .createdAt(tree.getCreatedAt())
                .ownerNickname(tree.getOwner().getNickname())
                .build();
    }
}