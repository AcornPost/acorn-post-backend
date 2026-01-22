package yerong.acorn_post_backend.tree.dto;

public record TreeEventPayload(
        String type,
        TreeLetterPayload letter,
        TreeStatsPayload stats,
        TreeMetaPayload tree
) {}
