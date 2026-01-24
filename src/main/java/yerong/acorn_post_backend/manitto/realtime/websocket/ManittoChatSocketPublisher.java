package yerong.acorn_post_backend.manitto.realtime.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import yerong.acorn_post_backend.manitto.realtime.kafka.ManittoChatEvent;

@Component
@RequiredArgsConstructor
public class ManittoChatSocketPublisher {

    private final SimpMessagingTemplate template;

    public void publish(Long roomId, ManittoChatEvent event) {
        template.convertAndSend("/topic/manitto.chat.rooms." + roomId, event);
    }
}
