package yerong.acorn_post_backend.rolling.realtime.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.common.security.CurrentMemberIdResolver;
import yerong.acorn_post_backend.group.repository.GroupMemberRepository;
import yerong.acorn_post_backend.rolling.dto.MessageDetailResponse;
import yerong.acorn_post_backend.rolling.dto.RollingMessagePayload;
import yerong.acorn_post_backend.rolling.dto.RollingPaperEventPayload;
import yerong.acorn_post_backend.rolling.realtime.event.RollingEvent;
import yerong.acorn_post_backend.rolling.realtime.pubsub.RollingMessageEventBus;

@Controller
@RequiredArgsConstructor
public class RollingSubscription {

    private final RollingMessageEventBus eventBus;
    private final GroupMemberRepository groupMemberRepository;
    private final CurrentMemberIdResolver currentMemberIdResolver;

    @SubscriptionMapping
    public Flux<RollingPaperEventPayload> rollingPaperEvents(
            @Argument Long groupId,
            @Argument Long toMemberId
    ) {
        Long memberId = currentMemberIdResolver.get();
        if (!groupMemberRepository.existsByGroup_IdAndMember_Id(groupId, memberId)) {
            return Flux.error(new ApiException(ErrorCode.ROLLING_GROUP_MEMBER_ONLY));
        }

        return eventBus.flux()
                .filter(e -> e.groupId().equals(groupId) && e.toMemberId().equals(toMemberId))
                .map(e -> toEventPayload(e));
    }

    private RollingPaperEventPayload toEventPayload(RollingEvent e) {
        return new RollingPaperEventPayload(
                e.type().name(),
                toPayload(e.payload())
        );
    }

    private RollingMessagePayload toPayload(MessageDetailResponse dto) {
        return new RollingMessagePayload(
                dto.id(),
                dto.fromMemberId(),
                dto.fromMemberName(),
                dto.content(),
                dto.color(),
                dto.shape() != null ? dto.shape().name() : null,
                dto.font(),
                dto.x(),
                dto.y(),
                dto.rotation()
        );
    }

}
