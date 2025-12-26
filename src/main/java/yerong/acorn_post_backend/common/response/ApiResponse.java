package yerong.acorn_post_backend.common.response;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        LocalDateTime timestamp
) {

    /* ===================== SUCCESS ===================== */

    public static <T> ApiResponse<T> success(SuccessCode code, T data) {
        return new ApiResponse<>(
                true,
                code.name(),
                code.getMessage(),
                data,
                LocalDateTime.now()
        );
    }

    public static ApiResponse<Void> success(SuccessCode code) {
        return success(code, null);
    }

    /* ===================== FAILURE ===================== */

    public static ApiResponse<Void> fail(ErrorCode code) {
        return new ApiResponse<>(
                false,
                code.name(),
                code.getMessage(),
                null,
                LocalDateTime.now()
        );
    }

    public static ApiResponse<Void> fail(ErrorCode code, String customMessage) {
        return new ApiResponse<>(
                false,
                code.name(),
                customMessage,
                null,
                LocalDateTime.now()
        );
    }
}
