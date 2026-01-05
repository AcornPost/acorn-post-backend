package yerong.acorn_post_backend.tree.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import yerong.acorn_post_backend.tree.domain.Letter;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    List<Letter> findAllByOrderByCreatedAtDesc();
    long countByIsReadFalse();
}
