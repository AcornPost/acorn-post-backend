package yerong.acorn_post_backend.tree.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yerong.acorn_post_backend.tree.domain.Letter;
import yerong.acorn_post_backend.tree.domain.LetterStatus;
import yerong.acorn_post_backend.tree.domain.Tree;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    List<Letter> findAllByOrderByCreatedAtDesc();
    List<Letter> findByTreeAndStatusOrderByCreatedAtDesc(Tree tree, LetterStatus status);
    List<Letter> findByTreeOrderByCreatedAtDesc(Tree tree);
    long countByIsReadFalse();
    long countByTreeAndIsReadFalse(Tree tree);
    long countByTreeAndStatus(Tree tree, LetterStatus status);
}
