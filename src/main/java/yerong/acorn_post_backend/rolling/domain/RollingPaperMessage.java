package yerong.acorn_post_backend.rolling.domain;

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
@Table(
        name = "rolling_paper_messages",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rolling_message_from_to",
                columnNames = {"group_id", "from_member_id", "to_member_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RollingPaperMessage extends BaseTimeEntity {
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

    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false, length = 20)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StickerShape shape;

    @Column(nullable = false, length = 50)
    private String font;

    @Column(nullable = false)
    private Double positionX;

    @Column(nullable = false)
    private Double positionY;

    @Column(nullable = false)
    private Double rotation;

    private RollingPaperMessage(
            Group group,
            Member fromMember,
            Member toMember,
            String content,
            String color,
            StickerShape shape,
            String font,
            Double positionX,
            Double positionY,
            Double rotation
    ) {
        this.group = group;
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.content = content;
        this.color = color;
        this.shape = shape;
        this.font = font;
        this.positionX = positionX;
        this.positionY = positionY;
        this.rotation = rotation;
    }
    public static RollingPaperMessage create(
            Group group,
            Member fromMember,
            Member toMember,
            String content,
            String color,
            StickerShape shape,
            String font,
            Double positionX,
            Double positionY,
            Double rotation
    ) {
        return new RollingPaperMessage(
                group, fromMember, toMember, content,
                color, shape, font, positionX, positionY, rotation
        );
    }
    public void updatePosition(Double x, Double y) {
        this.positionX = x;
        this.positionY = y;
    }

    public void updateContent(
            String content,
            String color,
            StickerShape shape,
            String font
    ) {
        this.content = content;
        this.color = color;
        this.shape = shape;
        this.font = font;
    }
}
