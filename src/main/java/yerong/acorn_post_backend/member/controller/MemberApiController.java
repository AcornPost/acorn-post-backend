package yerong.acorn_post_backend.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yerong.acorn_post_backend.member.dto.MemberNicknameRequest;
import yerong.acorn_post_backend.member.service.MemberService;
import yerong.acorn_post_backend.oauth.exception.JwtAuthException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberApiController {
    private final MemberService memberService;
    @PatchMapping("/nickname")
    public ResponseEntity<Void> updateNickname(@RequestBody MemberNicknameRequest request) {
        Long memberId = getCurrentMemberId();

        log.info("닉네임 변경 요청 - ID: {}, NewNickname: {}", memberId, request.getNickname());
        memberService.updateNickname(memberId, request.getNickname());

        return ResponseEntity.ok().build();
    }

    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new JwtAuthException("인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        } else if (principal instanceof String) {
            return Long.parseLong((String) principal);
        } else if (principal instanceof UserDetails) {
            return Long.parseLong(((UserDetails) principal).getUsername());
        } else {
            throw new IllegalArgumentException("알 수 없는 인증 타입입니다: " + principal.getClass().getName());
        }
    }
}
