package yerong.acorn_post_backend.manitto.realtime.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManittoChatEventProducer {
    @Value("${app.kafka.topic.manitto-chat:manitto.chat.v1}")
    private String topic;    private final KafkaTemplate<String, ManittoChatEvent> kafkaTemplate;

    public void publishMessage(Long messageId, Long roomId, Long senderMemberId) {
        kafkaTemplate.send(topic, String.valueOf(roomId),
                new ManittoChatEvent("MESSAGE", roomId, messageId, senderMemberId));
    }

    public void publishRead(Long roomId, Long readerMemberId, Long lastReadMessageId) {
        kafkaTemplate.send(topic, String.valueOf(roomId),
                new ManittoChatEvent("READ", roomId, lastReadMessageId, readerMemberId));
    }
}
