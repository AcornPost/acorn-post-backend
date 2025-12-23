package yerong.acorn_post_backend.oauth.exception;

public class JwtAuthException extends RuntimeException {
    public JwtAuthException(String message) { super(message); }
}