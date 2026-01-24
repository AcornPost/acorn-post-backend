package yerong.acorn_post_backend.manitto.realtime.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import yerong.acorn_post_backend.manitto.realtime.websocket.ManittoChatSocketPublisher;

@Component
@RequiredArgsConstructor
public class ManittoChatEventConsumer {

    private final ManittoChatSocketPublisher publisher;

    @KafkaListener(
            topics = "${app.kafka.topic.manitto-chat:manitto.chat.v1}",
            groupId = "${app.kafka.group.manitto-chat:acorn-post-manitto-chat}"
    )    public void onMessage(ManittoChatEvent event) {
        publisher.publish(event.roomId(), event);
    }
}