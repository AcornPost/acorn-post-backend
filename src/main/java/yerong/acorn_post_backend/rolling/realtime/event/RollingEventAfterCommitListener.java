package yerong.acorn_post_backend.rolling.realtime.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import yerong.acorn_post_backend.rolling.realtime.pubsub.RollingMessageEventBus;

@Slf4j
@Component
@RequiredArgsConstructor
public class RollingEventAfterCommitListener {

    private final RollingMessageEventBus eventBus;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(RollingEvent event) {
        Long messageId = (event.payload() != null) ? event.payload().id() : null;

        log.info("[PUBLISH_AFTER_COMMIT] type={}, groupId={}, toMemberId={}, messageId={}, subs={}",
                event.type(), event.groupId(), event.toMemberId(), messageId, eventBus.subscribers());

        eventBus.publish(event);
    }
}
