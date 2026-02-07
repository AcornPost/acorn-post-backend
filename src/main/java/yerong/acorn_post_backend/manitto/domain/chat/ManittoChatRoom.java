package yerong.acorn_post_backend.manitto.domain.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.common.domain.BaseTimeEntity;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.member.domain.Member;

@Entity
@Table(name = "manitto_chat_rooms",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_manitto_room_edge",
                columnNames = {"group_id","giver_id","receiver_id"}
        ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManittoChatRoom extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "giver_id", nullable = false)
    private Member giver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_low_id", nullable = false)
    private Member memberLow;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_high_id", nullable = false)
    private Member memberHigh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ManittoChatRoomStatus status;

    @Column(name = "last_read_message_low_id")
    private Long lastReadMessageLowId;

    @Column(name = "last_read_message_high_id")
    private Long lastReadMessageHighId;

    private ManittoChatRoom(Group group, Member giver, Member receiver) {
        this.group = group;
        this.giver = giver;
        this.receiver = receiver;
        this.status = ManittoChatRoomStatus.ACTIVE;

        Member low = giver.getId() < receiver.getId() ? giver : receiver;
        Member high = giver.getId() < receiver.getId() ? receiver : giver;
        this.memberLow = low;
        this.memberHigh = high;
    }

    public static ManittoChatRoom create(Group group, Member giver, Member receiver) {
        return new ManittoChatRoom(group, giver, receiver);
    }

    public boolean contains(Long memberId) {
        return memberLow.getId().equals(memberId) || memberHigh.getId().equals(memberId);
    }


    public void reveal() { this.status = ManittoChatRoomStatus.CLOSED; }

    public Long getLastReadMessageId(Long memberId) {
        if (memberLow.getId().equals(memberId)) return lastReadMessageLowId;
        if (memberHigh.getId().equals(memberId)) return lastReadMessageHighId;
        return null;
    }

    public void updateLastRead(Long memberId, Long messageId) {
        if (memberLow.getId().equals(memberId)) lastReadMessageLowId = messageId;
        else if (memberHigh.getId().equals(memberId)) lastReadMessageHighId = messageId;
    }
}
