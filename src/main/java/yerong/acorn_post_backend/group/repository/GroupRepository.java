package yerong.acorn_post_backend.group.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import yerong.acorn_post_backend.group.domain.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByJoinCode(String joinCode);
    boolean existsByJoinCode(String joinCode);
    List<Group> findAllByHost_IdOrderByCreatedAtDesc(Long memberId);
}
