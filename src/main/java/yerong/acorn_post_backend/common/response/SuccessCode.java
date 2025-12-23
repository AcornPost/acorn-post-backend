package yerong.acorn_post_backend.common.response;

import org.springframework.http.HttpStatus;

public enum SuccessCode {

    // ===== Auth =====
    NAVER_LOGIN_SUCCESS(HttpStatus.OK, "네이버 로그인이 성공되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃이 성공되었습니다."),

    // ===== Member =====
    NICKNAME_UPDATE_SUCCESS(HttpStatus.OK, "닉네임 변경이 성공되었습니다."),

    OK(HttpStatus.OK, "요청이 성공되었습니다."),
    CREATED(HttpStatus.CREATED, "생성이 성공되었습니다.");

    private final HttpStatus status;
    private final String message;

    SuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
