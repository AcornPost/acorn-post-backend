package yerong.acorn_post_backend.group.domain;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.common.domain.BaseTimeEntity;
import yerong.acorn_post_backend.member.domain.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "acorn_groups",
        uniqueConstraints = @UniqueConstraint(name = "uk_groups_join_code", columnNames = "join_code")
)
@Entity
public class Group extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupType type;

    @Column(name = "join_code", nullable = false, length = 10)
    private String joinCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_member_id", nullable = false)
    private Member host;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupStatus status;

    private Group(String name, String description, LocalDateTime deadline, GroupType type, String joinCode, Member host) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.type = type;
        this.joinCode = joinCode;
        this.host = host;
        this.status = GroupStatus.OPEN;
    }

    public static Group create(String name, String description, LocalDateTime deadline, GroupType type, String joinCode, Member host) {
        return new Group(name, description, deadline, type, joinCode, host);
    }

    public boolean isOpen() {
        return this.status == GroupStatus.OPEN;
    }

    public boolean isExpired(LocalDateTime now) {
        return deadline.isBefore(now);
    }

    public void close() {
        this.status = GroupStatus.CLOSED;
    }

    public void delete() {
        this.status = GroupStatus.DELETED;
    }
}
