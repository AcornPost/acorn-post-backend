package yerong.acorn_post_backend.rolling.dto;

import java.util.ArrayList;
import java.util.List;

public record MemberPaperResponse(
        Long memberId,
        String memberName,
        String deadline,
        String paperBgColor,
        List<MessageDetailResponse> messages
) {
    public MemberPaperResponse(Long memberId, String memberName, String deadline, String paperBgColor) {
        this(memberId, memberName, deadline, paperBgColor, new ArrayList<>());
    }
}
