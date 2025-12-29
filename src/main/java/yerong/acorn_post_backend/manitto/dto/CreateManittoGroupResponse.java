package yerong.acorn_post_backend.manitto.dto;

public record CreateManittoGroupResponse(
        Long groupId,
        String name,
        String joinCode
) {
}
