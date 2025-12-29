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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.common.domain.BaseTimeEntity;
import yerong.acorn_post_backend.group.domain.Group;

@Entity
@Table(name = "manitto_rounds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManittoRound extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ManittoRoundStatus status;

    private ManittoRound(Group group, Integer roundNumber) {
        this.group = group;
        this.roundNumber = roundNumber;
        this.startedAt = LocalDateTime.now();
        this.status = ManittoRoundStatus.IN_PROGRESS;
    }

    public static ManittoRound create(Group group, Integer roundNumber) {
        return new ManittoRound(group, roundNumber);
    }

    public void complete() {
        this.status = ManittoRoundStatus.COMPLETED;
        this.endedAt = LocalDateTime.now();
    }

    public boolean isInProgress() {
        return this.status == ManittoRoundStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this.status == ManittoRoundStatus.COMPLETED;
    }
}
