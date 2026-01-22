package yerong.acorn_post_backend.tree.realtime.event;

public record TreeDomainEvent(
        String shareCode,
        TreeEventType type,
        Long letterId
) {}
