package yerong.acorn_post_backend.manitto.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yerong.acorn_post_backend.manitto.domain.ManittoMission;

public interface ManittoMissionRepository extends JpaRepository<ManittoMission, Long> {

    @Query("""
        select mm from ManittoMission mm
        join fetch mm.match m
        join fetch m.group g
        where mm.id = :missionId
    """)
    Optional<ManittoMission> findByIdFetchMatchAndGroup(@Param("missionId") Long missionId);

    @Query("""
        select case when count(mm) > 0 then true else false end
        from ManittoMission mm
        join mm.match m
        where mm.id = :missionId and m.giver.id = :memberId
    """)
    boolean isOwnedByGiver(@Param("missionId") Long missionId, @Param("memberId") Long memberId);
}