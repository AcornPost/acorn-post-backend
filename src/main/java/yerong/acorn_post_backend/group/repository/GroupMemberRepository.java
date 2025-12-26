package yerong.acorn_post_backend.group.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yerong.acorn_post_backend.group.domain.GroupMember;
import yerong.acorn_post_backend.group.domain.GroupMemberRole;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);
    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, Long memberId);
    @Query("select gm.role from GroupMember gm where gm.group.id = :groupId and gm.member.id = :memberId")
    GroupMemberRole findRoleByGroupIdAndMemberId(Long groupId, Long memberId);
    boolean existsByGroup_IdAndMember_Id(Long groupId, Long memberId);

}
