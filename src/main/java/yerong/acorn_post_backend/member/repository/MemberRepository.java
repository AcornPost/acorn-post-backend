package yerong.acorn_post_backend.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.member.domain.SocialProvider;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByProviderAndSocialId(SocialProvider provider, String socialId);
}
