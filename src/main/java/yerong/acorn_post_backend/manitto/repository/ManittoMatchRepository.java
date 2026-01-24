package yerong.acorn_post_backend.manitto.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.manitto.domain.ManittoMatch;
import yerong.acorn_post_backend.member.domain.Member;

public interface ManittoMatchRepository extends JpaRepository<ManittoMatch, Long> {
    List<ManittoMatch> findByGroup(Group group);
    Optional<ManittoMatch> findByGroupAndGiver(Group group, Member giver);
    boolean existsByGroup(Group group);
    @Query("""
    select m from ManittoMatch m
    left join fetch m.missions
    where m.group = :group and m.giver = :giver
""")
    Optional<ManittoMatch> findByGroupAndGiverFetchMissions(@Param("group") Group group, @Param("giver") Member giver);
    Optional<ManittoMatch> findByGroupAndReceiver(Group group, Member receiver);
}