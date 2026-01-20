package yerong.acorn_post_backend.tree.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.tree.domain.Tree;

@Repository
public interface TreeRepository extends JpaRepository<Tree, Long> {
    Optional<Tree> findByShareCode(String shareCode);
    Optional<Tree> findByOwner(Member owner);
    boolean existsByShareCode(String shareCode);
}