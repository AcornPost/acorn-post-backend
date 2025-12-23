package yerong.acorn_post_backend.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yerong.acorn_post_backend.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
