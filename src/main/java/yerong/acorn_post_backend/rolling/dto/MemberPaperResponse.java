package yerong.acorn_post_backend.rolling.dto;

import java.util.ArrayList;
import java.util.List;

public record MemberPaperResponse(
        Long memberId,
        String memberName,
        String deadline,
        List<MessageDetailResponse> messages
) {
    public MemberPaperResponse(Long memberId, String memberName, String deadline) {
        this(memberId, memberName, deadline, new ArrayList<>());
    }
}
