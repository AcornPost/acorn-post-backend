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

    // ===== Rolling Paper =====
    ROLLING_GROUP_MEMBERS_FETCHED(HttpStatus.OK, "롤링페이퍼 그룹 멤버 조회가 성공되었습니다."),
    ROLLING_MEMBER_PAPER_FETCHED(HttpStatus.OK, "롤링페이퍼 편지 조회가 성공되었습니다."),
    ROLLING_MESSAGE_CREATED(HttpStatus.CREATED, "롤링페이퍼 편지 작성이 성공되었습니다."),
    ROLLING_MESSAGE_POSITION_UPDATED(HttpStatus.OK, "롤링페이퍼 편지 위치 변경이 성공되었습니다."),
    ROLLING_MESSAGE_DELETED(HttpStatus.OK, "롤링페이퍼 편지 삭제가 성공되었습니다."),
    ROLLING_MESSAGE_UPDATED(HttpStatus.OK, "롤링페이퍼 편지 수정이 성공되었습니다."), // [수정 완료]

    MANITTO_MY_GROUPS_FETCHED(HttpStatus.OK, "마니또 그룹 목록 조회 성공"),
    MANITTO_ROOM_INFO_FETCHED(HttpStatus.OK, "마니또 방 정보 조회 성공"),
    MANITTO_MATCHING_STARTED(HttpStatus.OK, "마니또 매칭이 시작되었습니다."),
    MANITTO_MESSAGE_SENT(HttpStatus.CREATED, "마니또 메시지가 전송되었습니다."),
    MANITTO_MESSAGES_FETCHED(HttpStatus.OK, "마니또 메시지 조회가 성공되었습니다."),
    MANITTO_REVEALED(HttpStatus.OK, "마니또가 공개되었습니다."),
    MANITTO_ROOM_RESTARTED(HttpStatus.OK, "마니또 방이 재시작되었습니다."),
    MANITTO_LEFT_GROUP(HttpStatus.OK, "그룹을 성공적으로 나갔습니다."),
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