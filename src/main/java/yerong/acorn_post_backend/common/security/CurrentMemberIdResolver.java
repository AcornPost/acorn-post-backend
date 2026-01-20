package yerong.acorn_post_backend.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.oauth.exception.JwtAuthException;

@Component
public class CurrentMemberIdResolver {

    public Long get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new JwtAuthException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long v) {
            return v;
        }

        if (principal instanceof String v) {
            try {
                return Long.parseLong(v);
            } catch (NumberFormatException e) {
                throw new JwtAuthException(
                        ErrorCode.UNKNOWN_AUTH_TYPE,
                        "Principal이 숫자(memberId) 형식이 아닙니다."
                );
            }
        }

        throw new JwtAuthException(
                ErrorCode.UNKNOWN_AUTH_TYPE,
                "알 수 없는 Principal 타입입니다: " + principal.getClass().getName()
        );
    }

    /**
     * 로그인하지 않은 사용자의 경우 null을 반환
     * 공개 API에서 선택적 인증이 필요한 경우 사용
     */
    public Long getOrNull() {
        try {
            return get();
        } catch (JwtAuthException e) {
            return null;
        }
    }
}