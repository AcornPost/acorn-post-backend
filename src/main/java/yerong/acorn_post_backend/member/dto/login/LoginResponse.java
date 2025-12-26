package yerong.acorn_post_backend.member.dto.login;

import yerong.acorn_post_backend.member.domain.Role;

public record LoginResponse(
        Long memberId,
        boolean isExistingMember,
        String nickname,
        Role role
) { }