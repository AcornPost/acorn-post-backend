package yerong.acorn_post_backend.rolling.realtime.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import yerong.acorn_post_backend.rolling.realtime.event.RollingEvent;

@Component
@Slf4j
public class RollingMessageEventBus {

    private final Sinks.Many<RollingEvent> sink =
            Sinks.many().multicast().onBackpressureBuffer(1024, false);

    public void publish(RollingEvent event) {
        var result = sink.tryEmitNext(event);
        if (result.isFailure()) {
            log.warn("RollingEvent emit failed: {}", result);
        }
    }

    public Flux<RollingEvent> flux() {
        return sink.asFlux();
    }

    public long subscribers() {
        return sink.currentSubscriberCount();
    }
}
