package yerong.acorn_post_backend.common.graphql;

import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.support.AuthenticationExtractor;
import org.springframework.graphql.server.webmvc.AuthenticationWebSocketInterceptor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;
import yerong.acorn_post_backend.oauth.token.JwtTokenProvider;
import yerong.acorn_post_backend.oauth.token.TokenType;

@Configuration
public class GraphQLWebSocketAuthConfig {

    @Bean
    public AuthenticationWebSocketInterceptor authenticationWebSocketInterceptor(
            JwtTokenProvider jwtTokenProvider
    ) {
        AuthenticationExtractor extractor = payload -> {
            String auth = getAuth(payload);
            System.out.println("[WS INIT payload auth] " + auth);
            if(auth == null || auth.isBlank()) return Mono.empty();
            return Mono.just(new UsernamePasswordAuthenticationToken(null, auth));
        };

        AuthenticationManager manager = authentication -> {
            String auth = (String) authentication.getCredentials();
            Long memberId = resolveMemberId(jwtTokenProvider, auth);
            System.out.println("[WS AUTH] auth=" + auth + " memberId=" + memberId);
            if (memberId == null) throw new BadCredentialsException("Invalid WS JWT");
            return new UsernamePasswordAuthenticationToken(memberId, null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
        };

        return new AuthenticationWebSocketInterceptor(extractor, manager);
    }

    private static String getAuth(Map<String, Object> payload) {
        Object v = payload.get("authorization");
        if (v == null) v = payload.get("Authorization");
        if (v == null) v = payload.get(HttpHeaders.AUTHORIZATION);
        return (v instanceof String s) ? s : null;
    }
    private static Long resolveMemberId(JwtTokenProvider jwtTokenProvider, String authHeader) {
        if (authHeader == null) return null;
        String value = authHeader.trim();
        if (!value.startsWith("Bearer ")) return null;
        try {
            String token = value.substring(7);
            return jwtTokenProvider.getMemberId(token, TokenType.ACCESS);
        } catch (Exception e) {
            return null;
        }
    }
}
