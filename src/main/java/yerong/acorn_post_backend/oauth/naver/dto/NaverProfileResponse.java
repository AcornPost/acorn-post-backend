package yerong.acorn_post_backend.oauth.naver.dto;

public record NaverProfileResponse (
        String resultcode,
        String message,
        Response response
){ public record Response(
        String id,
        String email,
        String username
) {}
}
