package yerong.acorn_post_backend.manitto.repository.chat;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import yerong.acorn_post_backend.manitto.domain.chat.ManittoChatMessage;
import yerong.acorn_post_backend.manitto.domain.chat.ManittoChatRoom;

public interface ManittoChatMessageRepository extends JpaRepository<ManittoChatMessage, Long> {
    List<ManittoChatMessage> findTop50ByRoomOrderByCreatedAtDesc(ManittoChatRoom room);
}
