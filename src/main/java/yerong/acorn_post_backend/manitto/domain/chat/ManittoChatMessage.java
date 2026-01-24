package yerong.acorn_post_backend.manitto.domain.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.common.domain.BaseTimeEntity;
import yerong.acorn_post_backend.member.domain.Member;

@Entity
@Table(name = "manitto_chat_messages",
        indexes = @Index(name="idx_room_created", columnList="room_id, createdAt"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManittoChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private ManittoChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_member_id", nullable = false)
    private Member sender;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "read_at")
    private java.time.LocalDateTime readAt;

    private ManittoChatMessage(ManittoChatRoom room, Member sender, String content) {
        this.room = room;
        this.sender = sender;
        this.content = content;
    }

    public static ManittoChatMessage create(ManittoChatRoom room, Member sender, String content) {
        return new ManittoChatMessage(room, sender, content);
    }

    public void markRead() { this.readAt = java.time.LocalDateTime.now(); }
}
