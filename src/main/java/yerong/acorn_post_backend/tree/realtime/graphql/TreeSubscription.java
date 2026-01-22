package yerong.acorn_post_backend.tree.realtime.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import yerong.acorn_post_backend.tree.dto.TreeEventPayload;
import yerong.acorn_post_backend.tree.realtime.pubsub.TreeEventBus;

@Controller
@RequiredArgsConstructor
public class TreeSubscription {

    private final TreeEventBus eventBus;
    @SubscriptionMapping
    public Flux<TreeEventPayload> treeEvents(@Argument String shareCode) {
        return eventBus.flux()
                .filter(env -> env.shareCode().equals(shareCode))
                .map(TreeEventBus.TreeEventEnvelope::payload);
    }
}