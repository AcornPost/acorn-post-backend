package yerong.acorn_post_backend.rolling.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yerong.acorn_post_backend.rolling.domain.RollingPaperMessage;

public interface RollingPaperMessageRepository extends JpaRepository<RollingPaperMessage, Long> {
    List<RollingPaperMessage> findAllByGroup_IdAndToMember_Id(Long groupId, Long toMemberId);
    Optional<RollingPaperMessage> findByGroup_IdAndFromMember_IdAndToMember_Id(
            Long groupId, Long fromMemberId, Long toMemberId
    );
    @Query("SELECT m.toMember.id FROM RollingPaperMessage m " +
            "WHERE m.group.id = :groupId AND m.fromMember.id = :fromMemberId")
    List<Long> findToMemberIdsByGroupAndFromMember(
            @Param("groupId") Long groupId,
            @Param("fromMemberId") Long fromMemberId
    );
    boolean existsByGroup_IdAndFromMember_IdAndToMember_Id(
            Long groupId, Long fromMemberId, Long toMemberId
    );
}
