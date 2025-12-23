package yerong.acorn_post_backend.oauth.token.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private final StringRedisTemplate redis;

    @Override
    public void save(Long memberId, String refreshToken, long ttlMs) {
        redis.opsForValue().set(key(memberId), refreshToken, ttlMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public String get(Long memberId) {
        return redis.opsForValue().get(key(memberId));
    }

    @Override
    public void delete(Long memberId) {
        redis.delete(key(memberId));
    }

    private String key(Long memberId) {
        return "rt:member:" + memberId;
    }
}
