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
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // ===== Group =====
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다."),
    GROUP_CLOSED(HttpStatus.BAD_REQUEST, "이미 종료된 그룹입니다."),
    GROUP_EXPIRED(HttpStatus.BAD_REQUEST, "마감된 그룹입니다."),
    ALREADY_JOINED(HttpStatus.CONFLICT, "이미 그룹에 참여하고 있습니다."),
    JOIN_CODE_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "참여 코드 생성에 실패했습니다."),
    NOT_GROUP_MEMBER(HttpStatus.BAD_REQUEST, "해당 그룹의 멤버가 아닙니다."),

    // ===== Rolling Paper =====
    ROLLING_GROUP_ONLY(HttpStatus.BAD_REQUEST, "롤링페이퍼 그룹에서만 사용할 수 있는 기능입니다."),
    ROLLING_GROUP_MEMBER_ONLY(HttpStatus.FORBIDDEN, "그룹 멤버만 사용할 수 있는 기능입니다."),
    ROLLING_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "롤링페이퍼 메시지를 찾을 수 없습니다."),
    ROLLING_MESSAGE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 멤버에게 메시지를 작성했습니다."),

    // ===== Manitto =====
    MANITTO_GROUP_ONLY(HttpStatus.BAD_REQUEST, "마니또 그룹에서만 사용할 수 있는 기능입니다."),
    MANITTO_GROUP_MEMBER_ONLY(HttpStatus.FORBIDDEN, "마니또 그룹 멤버만 사용할 수 있는 기능입니다."),
    MANITTO_HOST_ONLY(HttpStatus.FORBIDDEN, "마니또 방장만 사용할 수 있는 기능입니다."),
    MANITTO_HOST_CANNOT_LEAVE(HttpStatus.BAD_REQUEST, "방장은 그룹을 나갈 수 없습니다. 방 폭파를 사용해주세요."),
    MANITTO_MIN_PARTICIPANTS_NOT_MET(HttpStatus.BAD_REQUEST, "참가자가 2명 이상일 때만 시작할 수 있습니다."),
    MANITTO_ROUND_IN_PROGRESS_EXISTS(HttpStatus.CONFLICT, "이미 진행 중인 라운드가 있습니다."),
    MANITTO_ROUND_NOT_FOUND(HttpStatus.BAD_REQUEST, "진행 중인 라운드를 찾을 수 없습니다."),
    MANITTO_PREVIOUS_ROUND_NOT_FOUND(HttpStatus.BAD_REQUEST, "이전 라운드를 찾을 수 없습니다."),
    MANITTO_ROUND_NOT_COMPLETED(HttpStatus.CONFLICT, "이전 라운드가 완료되지 않았습니다."),
    MANITTO_MATCH_NOT_FOUND(HttpStatus.BAD_REQUEST, "마니또 매칭 정보를 찾을 수 없습니다."),
    MANITTO_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "마니또 메시지를 찾을 수 없습니다."),
    MANITTO_MATCHING_STARTED_ALREADY(HttpStatus.BAD_REQUEST, "이미 매칭이 시작된 마니또 숲에는 입장할 수 없습니다."),

    // ===== Manitto Chat =====
    MANITTO_CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "마니또 채팅방을 찾을 수 없습니다."),
    MANITTO_CHAT_ROOM_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 채팅방에 접근할 권한이 없습니다."),
    MANITTO_CHAT_ROOM_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "활동이 종료되어 채팅을 보낼 수 없습니다."),
    MANITTO_CHAT_MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "메시지 내용을 입력해주세요."),
    MANITTO_CHAT_MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, "메시지는 500자 이내로 입력해주세요."),
    MANITTO_CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "마니또 채팅 메시지를 찾을 수 없습니다."),
    MANITTO_CHAT_READ_INVALID(HttpStatus.BAD_REQUEST, "읽음 처리 정보가 올바르지 않습니다."),

    // ===== Letter =====
    LETTER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "편지가 존재하지 않습니다."),
    TREE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 트리가 존재합니다."),
    TREE_NOT_FOUND(HttpStatus.NOT_FOUND, "트리를 찾을 수 없습니다."),
    LETTER_NOT_APPROVED(HttpStatus.FORBIDDEN, "승인되지 않은 편지입니다."),
    LETTER_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 처리된 편지입니다."),
    LETTER_INVALID_INPUT_NICKNAME(HttpStatus.BAD_REQUEST, "보내는 분의 이름을 입력해주세요."),
    LETTER_INVALID_INPUT_CONTENT(HttpStatus.BAD_REQUEST, "편지 내용을 입력해주세요."),
    // ===== Authorization =====
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "요청이 충돌했습니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 멤버에게 메시지를 작성했습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이 그룹에는 이미 같은 이름이 있습니다.");

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