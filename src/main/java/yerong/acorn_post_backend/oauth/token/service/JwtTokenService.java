package yerong.acorn_post_backend.oauth.token.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import yerong.acorn_post_backend.oauth.exception.JwtAuthException;
import yerong.acorn_post_backend.oauth.token.JwtTokenProvider;
import yerong.acorn_post_backend.oauth.token.TokenPair;

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
        // parse()에서 위조/만료 등 검증됨
        Long memberId = jwt.getMemberId(accessToken);
        String role = jwt.getRole(accessToken);

        return new UsernamePasswordAuthenticationToken(
                memberId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    @Override
    public long getRemainingMillis(String accessToken) {
        return jwt.remainingMillis(accessToken);
    }

    @Override
    public TokenPair reissue(String refreshToken) {
        Long memberId = jwt.getMemberId(refreshToken);
        String role = jwt.getRole(refreshToken);

        String saved = refreshStore.get(memberId);
        if (saved == null || !saved.equals(refreshToken)) {
            throw new JwtAuthException("INVALID_REFRESH_TOKEN");
        }

        String newAccess = jwt.createAccessToken(memberId, role);
        String newRefresh = jwt.createRefreshToken(memberId);

        refreshStore.save(memberId, newRefresh, jwt.refreshExpMs()); // rotation
        return new TokenPair(newAccess, newRefresh);
    }

    @Override
    public TokenPair issue(Long memberId, String role) {
        String access = jwt.createAccessToken(memberId, role);
        String refresh = jwt.createRefreshToken(memberId);
        refreshStore.save(memberId, refresh, jwt.refreshExpMs());
        return new TokenPair(access, refresh);
    }
    @Override
    public void logout(String accessToken, String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) return;

        Long memberId = jwt.getMemberId(refreshToken);
        refreshStore.delete(memberId);
    }
}