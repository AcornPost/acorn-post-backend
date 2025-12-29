package yerong.acorn_post_backend.manitto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.manitto.domain.ManittoMission;

public interface ManittoMissionRepository extends JpaRepository<ManittoMission, Long> {

    List<ManittoMission> findByGroupAndRoundNumberOrderByDisplayOrderAsc(Group group, Integer roundNumber);
}