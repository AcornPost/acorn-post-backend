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

@Entity
@Table(name = "manitto_missions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManittoMission extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    private ManittoMission(Group group, String content, Integer roundNumber, Integer displayOrder) {
        this.group = group;
        this.content = content;
        this.roundNumber = roundNumber;
        this.displayOrder = displayOrder;
    }

    public static ManittoMission create(Group group, String content, Integer roundNumber, Integer displayOrder) {
        return new ManittoMission(group, content, roundNumber, displayOrder);
    }
}
