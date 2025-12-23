package yerong.acorn_post_backend.oauth.naver.dto;

public record NaverUserInfo (
        String socialId,
        String email,
        String username
) {
}
