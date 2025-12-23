package yerong.acorn_post_backend.oauth.token.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import yerong.acorn_post_backend.oauth.token.TokenPair;

public interface TokenService {
    String resolveAccessToken(HttpServletRequest request);
    Authentication getAuthentication(String accessToken); // 유효성 검증 + auth 생성
    long getRemainingMillis(String accessToken); // 블랙리스트 TTL용
    TokenPair reissue(String refreshToken); // refresh 검증 후 재발급
    TokenPair issue(Long memberId, String role);
    void logout(String accessToken, String refreshToken);
}