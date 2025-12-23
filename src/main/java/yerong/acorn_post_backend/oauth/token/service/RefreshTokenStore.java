package yerong.acorn_post_backend.oauth.token.service;

public interface RefreshTokenStore {
    void save(Long memberId, String refreshToken, long ttlMs);
    String get(Long memberId);
    void delete(Long memberId);
}