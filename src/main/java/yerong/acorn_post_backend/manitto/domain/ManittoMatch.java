package yerong.acorn_post_backend.manitto.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import java.util.List;
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "manitto_match_missions",
            joinColumns = @JoinColumn(name = "match_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "mission", nullable = false)
    private List<MissionType> missions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ManittoMatchStatus status;

    private ManittoMatch(Group group, Member giver, Member receiver, List<MissionType> missions) {
        this.group = group;
        this.giver = giver;
        this.receiver = receiver;
        this.missions = missions;
        this.status = ManittoMatchStatus.ACTIVE;
    }

    public static ManittoMatch create(Group group, Member giver, Member receiver, List<MissionType> missions) {
        return new ManittoMatch(group, giver, receiver, missions);
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
