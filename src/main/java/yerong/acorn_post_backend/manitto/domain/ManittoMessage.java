package yerong.acorn_post_backend.manitto.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.common.domain.BaseTimeEntity;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.member.domain.Member;

@Entity
@Table(name = "manitto_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManittoMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_member_id", nullable = false)
    private Member toMember;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    private ManittoMessage(Group group, Member fromMember, Member toMember, String content, Integer roundNumber) {
        this.group = group;
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.content = content;
        this.roundNumber = roundNumber;
        this.isRead = false;
    }

    public static ManittoMessage create(Group group, Member fromMember, Member toMember, String content, Integer roundNumber) {
        return new ManittoMessage(group, fromMember, toMember, content, roundNumber);
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
