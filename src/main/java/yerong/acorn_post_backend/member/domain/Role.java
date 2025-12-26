package yerong.acorn_post_backend.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("ROLE_USER", "일반 사용자"),

    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;         // Spring Security 권한 문자열
    private final String description; // 설명(로그/관리화면 등에 사용)

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
