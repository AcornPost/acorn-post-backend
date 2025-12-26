package yerong.acorn_post_backend.common.response;

import org.springframework.http.HttpStatus;

public enum SuccessCode {

    // ===== Auth =====
    NAVER_LOGIN_SUCCESS(HttpStatus.OK, "네이버 로그인이 성공되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃이 성공되었습니다."),

    // ===== Member =====
    NICKNAME_UPDATE_SUCCESS(HttpStatus.OK, "닉네임 변경이 성공되었습니다."),

    GROUP_CREATED(HttpStatus.CREATED, "그룹 생성이 성공되었습니다."),
    GROUP_JOINED(HttpStatus.OK, "그룹 참여가 성공되었습니다."),
    GROUP_ALREADY_JOINED(HttpStatus.OK, "이미 참여한 숲입니다."),
    GROUP_LIST_FETCHED(HttpStatus.OK, "내 숲 목록 조회가 성공되었습니다."),

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
