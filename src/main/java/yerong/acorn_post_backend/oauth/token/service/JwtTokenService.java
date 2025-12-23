package yerong.acorn_post_backend.oauth.token.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.oauth.exception.JwtAuthException;
import yerong.acorn_post_backend.oauth.token.JwtTokenProvider;
import yerong.acorn_post_backend.oauth.token.TokenPair;
import yerong.acorn_post_backend.oauth.token.TokenType;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

    private final JwtTokenProvider jwt;
    private final RefreshTokenStore refreshStore;

    @Override
    public String resolveAccessToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer == null || !bearer.startsWith("Bearer ")) return null;
        return bearer.substring(7);
    }

    @Override
    public Authentication getAuthentication(String accessToken) {
        Long memberId = jwt.getMemberId(accessToken, TokenType.ACCESS);
        String role = jwt.getRole(accessToken, TokenType.ACCESS);

        return new UsernamePasswordAuthenticationToken(
                memberId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    @Override
    public long getRemainingMillis(String accessToken) {
        return jwt.remainingMillis(accessToken, TokenType.ACCESS);
    }

    @Override
    public TokenPair reissue(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new JwtAuthException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        Long memberId = jwt.getMemberId(refreshToken, TokenType.REFRESH);
        String role = jwt.getRole(refreshToken, TokenType.REFRESH);

        String saved = refreshStore.get(memberId);
        if (saved == null) {
            throw new JwtAuthException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (!saved.equals(refreshToken)) {
            throw new JwtAuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccess = jwt.createAccessToken(memberId, role);
        String newRefresh = jwt.createRefreshToken(memberId, role);

        refreshStore.save(memberId, newRefresh, jwt.refreshExpMs());
        return new TokenPair(newAccess, newRefresh);
    }

    @Override
    public TokenPair issue(Long memberId, String role) {
        String access = jwt.createAccessToken(memberId, role);
        String refresh = jwt.createRefreshToken(memberId, role);

        refreshStore.save(memberId, refresh, jwt.refreshExpMs());
        return new TokenPair(access, refresh);
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;

        Long memberId = jwt.getMemberId(refreshToken, TokenType.REFRESH);
        refreshStore.delete(memberId);
    }
}
