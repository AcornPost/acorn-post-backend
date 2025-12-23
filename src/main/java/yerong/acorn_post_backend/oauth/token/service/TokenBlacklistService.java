package yerong.acorn_post_backend.oauth.token.service;

public interface TokenBlacklistService {
    boolean isBlacklisted(String accessToken);
    void blacklist(String accessToken, long ttlMillis);
}