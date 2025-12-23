package yerong.acorn_post_backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.member.domain.Member;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String email;
    private String nickname;
    private String role;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname()) // 혹은 member.getNickname()
                .role(member.getRole().name())
                .build();
    }
}