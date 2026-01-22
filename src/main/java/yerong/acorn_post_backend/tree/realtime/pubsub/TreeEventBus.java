package yerong.acorn_post_backend.tree.realtime.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import yerong.acorn_post_backend.tree.dto.TreeEventPayload;

@Slf4j
@Component
public class TreeEventBus {

    private final Sinks.Many<TreeEventEnvelope> sink =
            Sinks.many().multicast().onBackpressureBuffer(1024, false);

    public void publish(String shareCode, TreeEventPayload payload) {
        var r = sink.tryEmitNext(new TreeEventEnvelope(shareCode, payload));
        if (r.isFailure()) log.warn("TreeEvent emit failed: {}", r);
    }

    public Flux<TreeEventEnvelope> flux() {
        return sink.asFlux();
    }

    public long subscribers() {
        return sink.currentSubscriberCount();
    }

    public record TreeEventEnvelope(String shareCode, TreeEventPayload payload) {}
}
