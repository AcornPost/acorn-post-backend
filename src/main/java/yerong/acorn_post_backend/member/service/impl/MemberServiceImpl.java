package yerong.acorn_post_backend.member.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.member.domain.SocialProvider;
import yerong.acorn_post_backend.member.dto.MemberResponse;
import yerong.acorn_post_backend.member.dto.login.LoginResponse;
import yerong.acorn_post_backend.member.repository.MemberRepository;
import yerong.acorn_post_backend.member.service.MemberService;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public LoginResponse findOrCreateSocialMember(SocialProvider provider, String socialId, String email, String username) {
        return memberRepository.findByProviderAndSocialId(provider, socialId)
                .map(m -> new LoginResponse(m.getId(), true, m.getUsername(), m.getRole()))
                .orElseGet(() -> {
                    Member saved = memberRepository.save(new Member(email, username, null, provider, socialId));
                    return new LoginResponse(saved.getId(), false, saved.getNickname(), saved.getRole());
                });
    }

    @Override
    public MemberResponse findById(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return MemberResponse.from(member);
    }

    @Override
    @Transactional
    public void updateNickname(Long memberId, String newNickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member.updateNickname(newNickname);
    }
}
