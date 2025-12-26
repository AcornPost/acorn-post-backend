package yerong.acorn_post_backend.oauth.token.service;


import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenBlacklistService implements TokenBlacklistService {

    private final StringRedisTemplate redis;

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redis.hasKey(key(token)));
    }

    @Override
    public void blacklist(String token, long ttlMillis) {
        redis.opsForValue().set(key(token), "1", ttlMillis, TimeUnit.MILLISECONDS);
    }

    private String key(String token) { return "bl:at:" + token; }
}