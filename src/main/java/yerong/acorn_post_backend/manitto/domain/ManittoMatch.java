package yerong.acorn_post_backend.manitto.domain;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.common.domain.BaseTimeEntity;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.member.domain.Member;

@Entity
@Table(name = "manitto_matches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManittoMatch extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "giver_member_id", nullable = false)
    private Member giver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_member_id", nullable = false)
    private Member receiver;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ManittoMatchStatus status;

    private ManittoMatch(Group group, Member giver, Member receiver, Integer roundNumber) {
        this.group = group;
        this.giver = giver;
        this.receiver = receiver;
        this.roundNumber = roundNumber;
        this.status = ManittoMatchStatus.ACTIVE;
    }

    public static ManittoMatch create(Group group, Member giver, Member receiver, Integer roundNumber) {
        return new ManittoMatch(group, giver, receiver, roundNumber);
    }

    public void reveal() {
        this.status = ManittoMatchStatus.REVEALED;
    }

    public boolean isActive() {
        return this.status == ManittoMatchStatus.ACTIVE;
    }

    public boolean isRevealed() {
        return this.status == ManittoMatchStatus.REVEALED;
    }
}
