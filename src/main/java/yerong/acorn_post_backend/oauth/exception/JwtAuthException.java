package yerong.acorn_post_backend.oauth.exception;

import yerong.acorn_post_backend.common.response.ErrorCode;

public class JwtAuthException extends RuntimeException {
    private final ErrorCode errorCode;

    public JwtAuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public JwtAuthException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
