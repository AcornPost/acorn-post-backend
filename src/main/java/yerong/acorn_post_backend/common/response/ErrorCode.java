package yerong.acorn_post_backend.common.response;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // ===== Common =====
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),

    // ===== Auth / Security =====
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다."),
    UNKNOWN_AUTH_TYPE(HttpStatus.BAD_REQUEST, "알 수 없는 인증 타입입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 없습니다."),

    // ===== OAuth (Naver) =====
    NAVER_TOKEN_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "네이버 토큰 받아오기에 실패했습니다."),
    NAVER_PROFILE_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "네이버 프로필 받아오기에 실패했습니다."),

    // ===== Member =====
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),

    // ===== Group =====
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다."),
    GROUP_CLOSED(HttpStatus.BAD_REQUEST, "이미 종료된 그룹입니다."),
    GROUP_EXPIRED(HttpStatus.BAD_REQUEST, "마감된 그룹입니다."),
    ALREADY_JOINED(HttpStatus.CONFLICT, "이미 그룹에 참여하고 있습니다."),
    JOIN_CODE_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "참여 코드 생성에 실패했습니다."),

    // ===== Authorization =====
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청이 충돌했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
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