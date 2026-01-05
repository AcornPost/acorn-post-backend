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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.common.domain.BaseTimeEntity;
import yerong.acorn_post_backend.member.domain.Member;

@Entity
@Table(
        name = "group_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_group_members_group_member", columnNames = {"group_id", "member_id"}),
                @UniqueConstraint(name = "uk_group_members_group_nickname", columnNames = {"group_id", "groupNickname"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String groupNickname;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupMemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GroupMemberStatus status;

    private GroupMember(Group group, String niclname, Member member, GroupMemberRole role) {
        this.group = group;
        this.groupNickname = niclname;
        this.member = member;
        this.role = role;
        this.status = GroupMemberStatus.ACTIVE;
    }

    public static GroupMember host(Group group, Member member, String groupNickname) {
        return new GroupMember(group, groupNickname, member, GroupMemberRole.HOST);
    }

    public static GroupMember member(Group group, Member member, String groupNickname) {
        return new GroupMember(group, groupNickname, member, GroupMemberRole.MEMBER);
    }

    public void updateNickname(String newNickname) {
        this.groupNickname = newNickname;
    }

    public boolean isActive() {
        return this.status == GroupMemberStatus.ACTIVE;
    }

    public boolean isHost() {
        return this.role == GroupMemberRole.HOST;
    }

    public void leave() {
        this.status = GroupMemberStatus.LEFT;
    }
}
