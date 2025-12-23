package yerong.acorn_post_backend.oauth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yerong.acorn_post_backend.common.response.ApiResponse;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.common.response.SuccessCode;
import yerong.acorn_post_backend.common.security.CurrentMemberIdResolver;
import yerong.acorn_post_backend.oauth.exception.JwtAuthException;
import yerong.acorn_post_backend.member.dto.MemberResponse;
import yerong.acorn_post_backend.member.service.MemberService;
import yerong.acorn_post_backend.oauth.token.TokenPair;
import yerong.acorn_post_backend.oauth.token.TokenType;
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
    private final CurrentMemberIdResolver currentMember;

    @GetMapping("/token")
    public ApiResponse<TokenPair> token(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("토큰 재발급 실패: Refresh Token이 쿠키에 없습니다.");
            throw new JwtAuthException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        log.info("Access Token 재발급 요청");
        TokenPair pair = tokenService.reissue(refreshToken);
        return ApiResponse.success(SuccessCode.OK, pair);
    }
    @GetMapping("/profile")
    public ApiResponse<MemberResponse> getProfile() {
        Long memberId = currentMember.get();

        log.info("프로필 조회 요청. Member ID: {}", memberId);
        MemberResponse profile = memberService.findById(memberId);

        return ApiResponse.success(SuccessCode.OK, profile);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String bearer,
                                  HttpServletResponse response,
                                  @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        log.info("로그아웃 요청");

        String at = resolveBearerTokenOrThrow(bearer);

        long remaining = tokenService.getRemainingMillis(at);
        if (remaining > 0) {
            blacklistService.blacklist(at, remaining);
        }

        tokenService.logout(at, refreshToken);

        ResponseCookie delete = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", delete.toString());

        return ApiResponse.success(SuccessCode.LOGOUT_SUCCESS);
    }

    private Long getAuthenticatedMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new JwtAuthException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long v) return v;

        if (principal instanceof String v) {
            try {
                return Long.parseLong(v);
            } catch (NumberFormatException e) {
                throw new JwtAuthException(ErrorCode.UNKNOWN_AUTH_TYPE);
            }
        }
        throw new JwtAuthException(ErrorCode.UNKNOWN_AUTH_TYPE);
    }

    private String resolveBearerTokenOrThrow(String bearer) {
        if (bearer == null || bearer.isBlank() || !bearer.startsWith("Bearer ")) {
            throw new JwtAuthException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        String token = bearer.substring(7).trim();
        if (token.isEmpty()) {
            throw new JwtAuthException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        return token;
    }
}
