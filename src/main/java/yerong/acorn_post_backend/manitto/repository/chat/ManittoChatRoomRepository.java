package yerong.acorn_post_backend.manitto.repository.chat;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yerong.acorn_post_backend.group.domain.Group;
import yerong.acorn_post_backend.manitto.domain.chat.ManittoChatRoom;
import yerong.acorn_post_backend.manitto.domain.chat.ManittoChatRoomStatus;
import yerong.acorn_post_backend.member.domain.Member;

public interface ManittoChatRoomRepository extends JpaRepository<ManittoChatRoom, Long> {
    @Modifying
    @Query("""
update ManittoChatRoom r
set r.status = :status
where r.group = :group and r.status <> :status
""")
    int updateStatusByGroup(@Param("group") Group group,
                            @Param("status") ManittoChatRoomStatus status);
    @Modifying
    @Query("""
update ManittoChatRoom r
set r.lastReadMessageLowId =
    case when r.memberLow.id = :memberId then :messageId else r.lastReadMessageLowId end,
    r.lastReadMessageHighId =
    case when r.memberHigh.id = :memberId then :messageId else r.lastReadMessageHighId end
where r.id = :roomId
""")
    int updateLastRead(@Param("roomId") Long roomId,
                       @Param("memberId") Long memberId,
                       @Param("messageId") Long messageId);

    Optional<ManittoChatRoom> findByGroupAndGiverAndReceiver(Group group, Member giver, Member receiver);
}