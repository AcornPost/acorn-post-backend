package yerong.acorn_post_backend.tree.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.common.domain.BaseTimeEntity;

@Entity
@Table(name = "letters")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Letter extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Double positionX;

    @Column(nullable = false)
    private Double positionY;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isMine = true;

    public void markAsRead() {
        this.isRead = true;
    }

    public void update(String nickname, String content) {
        this.nickname = nickname;
        this.content = content;
    }
}
