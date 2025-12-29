package yerong.acorn_post_backend.manitto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.manitto.domain.ManittoMessage;
import yerong.acorn_post_backend.member.domain.Member;

public interface ManittoMessageRepository extends JpaRepository<ManittoMessage, Long> {

    List<ManittoMessage> findByGroupAndRoundNumberAndToMemberOrderByCreatedAtAsc(
            Group group,
            Integer roundNumber,
            Member toMember
    );
}