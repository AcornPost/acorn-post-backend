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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "manitto_missions")
public class ManittoMission extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private ManittoMatch match;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission", nullable = false)
    private MissionType mission;

    @Column(nullable = false)
    private boolean checked;

    public void check(boolean value) { this.checked = value; }
    public ManittoMission(ManittoMatch match, MissionType mission) {
        this.match = match;
        this.mission = mission;
        this.checked = false;
    }

}
