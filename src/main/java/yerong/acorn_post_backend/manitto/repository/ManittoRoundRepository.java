package yerong.acorn_post_backend.manitto.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.manitto.domain.ManittoRound;
import yerong.acorn_post_backend.manitto.domain.ManittoRoundStatus;

public interface ManittoRoundRepository extends JpaRepository<ManittoRound, Long> {

    Optional<ManittoRound> findTopByGroupOrderByRoundNumberDesc(Group group);

    Optional<ManittoRound> findByGroupAndStatus(Group group, ManittoRoundStatus status);

    boolean existsByGroupAndStatus(Group group, ManittoRoundStatus status);

    boolean existsByGroup(Group group);
}