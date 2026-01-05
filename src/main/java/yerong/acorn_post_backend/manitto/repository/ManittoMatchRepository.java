package yerong.acorn_post_backend.manitto.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.data.jpa.repository.JpaRepository;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.manitto.domain.ManittoMatch;
import yerong.acorn_post_backend.member.domain.Member;

public interface ManittoMatchRepository extends JpaRepository<ManittoMatch, Long> {
    List<ManittoMatch> findByGroup(Group group);

    Optional<ManittoMatch> findByGroupAndGiver(Group group, Member giver);

    Optional<ManittoMatch> findByGroupAndReceiver(Group group, Member receiver);

    boolean existsByGroup(Group group);
}