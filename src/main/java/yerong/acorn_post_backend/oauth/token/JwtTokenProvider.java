package yerong.acorn_post_backend.oauth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import yerong.acorn_post_backend.oauth.exception.JwtAuthException;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessExpMs;
    private final long refreshExpMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-exp-ms:1800000}") long accessExpMs,   // 30분 기본
            @Value("${jwt.refresh-exp-ms:1209600000}") long refreshExpMs // 14일 기본
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;
    }

    public String createAccessToken(Long memberId, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExpMs);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshExpMs);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthException("INVALID_TOKEN");
        }
    }

    public long remainingMillis(String token) {
        Date exp = parse(token).getBody().getExpiration();
        return exp.getTime() - System.currentTimeMillis();
    }

    public Long getMemberId(String token) {
        return Long.valueOf(parse(token).getBody().getSubject());
    }

    public String getRole(String token) {
        Object role = parse(token).getBody().get("role");
        return role == null ? "USER" : role.toString();
    }

    public long refreshExpMs() { return refreshExpMs; }
}