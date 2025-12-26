package yerong.acorn_post_backend.member.service;

import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.member.domain.SocialProvider;
import yerong.acorn_post_backend.member.dto.MemberResponse;
import yerong.acorn_post_backend.member.dto.login.LoginResponse;

public interface MemberService {
    LoginResponse findOrCreateSocialMember(SocialProvider provider, String socialId, String email, String username);
    MemberResponse findById(Long memberId);
    void updateNickname(Long memberId, String newNickname);
}
