package yerong.acorn_post_backend.manitto.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.manitto.domain.ManittoMatch;
import yerong.acorn_post_backend.member.domain.Member;

public interface ManittoMatchRepository extends JpaRepository<ManittoMatch, Long> {

    List<ManittoMatch> findByGroupAndRoundNumber(Group group, Integer roundNumber);

    Optional<ManittoMatch> findByGroupAndRoundNumberAndGiver(Group group, Integer roundNumber, Member giver);

    Optional<ManittoMatch> findByGroupAndRoundNumberAndReceiver(Group group, Integer roundNumber, Member receiver);
}