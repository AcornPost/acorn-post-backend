package yerong.acorn_post_backend.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yerong.acorn_post_backend.common.response.ApiResponse;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.common.response.SuccessCode;
import yerong.acorn_post_backend.common.security.CurrentMemberIdResolver;
import yerong.acorn_post_backend.member.dto.MemberNicknameRequest;
import yerong.acorn_post_backend.member.service.MemberService;
import yerong.acorn_post_backend.oauth.exception.JwtAuthException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberApiController {

    private final MemberService memberService;
    private final CurrentMemberIdResolver currentMember;

    @PatchMapping("/nickname")
    public ApiResponse<Void> updateNickname(@RequestBody MemberNicknameRequest request) {
        Long memberId = currentMember.get();

        log.info("닉네임 변경 요청 - ID: {}, NewNickname: {}", memberId, request.getNickname());
        memberService.updateNickname(memberId, request.getNickname());

        return ApiResponse.success(SuccessCode.NICKNAME_UPDATE_SUCCESS);
    }

    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new JwtAuthException(ErrorCode.AUTHENTICATION_REQUIRED);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long v) return v;

        if (principal instanceof String v) {
            try {
                return Long.parseLong(v);
            } catch (NumberFormatException e) {
                throw new JwtAuthException(ErrorCode.UNKNOWN_AUTH_TYPE, "Principal이 숫자(memberId) 형식이 아닙니다.");
            }
        }

        throw new JwtAuthException(
                ErrorCode.UNKNOWN_AUTH_TYPE,
                "알 수 없는 Principal 타입입니다: " + principal.getClass().getName()
        );
    }
}
