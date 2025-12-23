package yerong.acorn_post_backend.oauth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import yerong.acorn_post_backend.member.dto.MemberResponse;
import yerong.acorn_post_backend.member.service.MemberService;
import yerong.acorn_post_backend.oauth.exception.JwtAuthException;
import yerong.acorn_post_backend.oauth.token.TokenPair;
import yerong.acorn_post_backend.oauth.token.service.TokenBlacklistService;
import yerong.acorn_post_backend.oauth.token.service.TokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final TokenService tokenService;
    private final TokenBlacklistService blacklistService;
    private final MemberService memberService;

    @GetMapping("/token")
    public TokenPair token(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("토큰 재발급 실패: Refresh Token이 쿠키에 없습니다.");
            throw new JwtAuthException("NO_REFRESH_TOKEN");
        }

        log.info("Access Token 재발급 요청");
        return tokenService.reissue(refreshToken);
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            log.error("프로필 조회 실패: SecurityContext에 인증 정보가 없습니다.");
            throw new JwtAuthException("인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();
        Long memberId;

        if (principal instanceof Long) {
            memberId = (Long) principal;
        }
        else if (principal instanceof String) {
            memberId = Long.parseLong((String) principal);
        }
        else if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            memberId = Long.parseLong(username);
        } else {
            log.error("프로필 조회 오류: 알 수 없는 Principal 타입입니다. Type: {}", principal.getClass().getName());
            throw new IllegalArgumentException("알 수 없는 인증 타입입니다: " + principal.getClass().getName());
        }

        log.info("프로필 조회 요청. Member ID: {}", memberId);

        MemberResponse response = memberService.findById(memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String bearer,
                       HttpServletResponse response,
                       @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        log.info("로그아웃 요청");

        String at = bearer.replace("Bearer ", "");
        blacklistService.blacklist(at, tokenService.getRemainingMillis(at));

        tokenService.logout(at, refreshToken);

        ResponseCookie delete = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", delete.toString());
    }
}