package yerong.acorn_post_backend.tree.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LetterRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 10)
        String nickname,

        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 260)
        String content
) {}
