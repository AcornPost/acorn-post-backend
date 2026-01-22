package yerong.acorn_post_backend.tree.realtime.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.tree.domain.Letter;
import yerong.acorn_post_backend.tree.domain.LetterStatus;
import yerong.acorn_post_backend.tree.domain.Tree;
import yerong.acorn_post_backend.tree.dto.*;
import yerong.acorn_post_backend.tree.realtime.pubsub.TreeEventBus;
import yerong.acorn_post_backend.tree.repository.LetterRepository;
import yerong.acorn_post_backend.tree.repository.TreeRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TreeEventAfterCommitListener {

    private final TreeEventBus eventBus;
    private final TreeRepository treeRepository;
    private final LetterRepository letterRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TreeDomainEvent e) {
        Tree tree = treeRepository.findByShareCode(e.shareCode())
                .orElseThrow(() -> new ApiException(ErrorCode.TREE_NOT_FOUND));

        TreeStatsPayload stats = buildStats(tree);
        TreeMetaPayload meta = new TreeMetaPayload(tree.getTitle());
        TreeLetterPayload letter = buildLetterPayload(e.letterId());

        TreeEventPayload payload = new TreeEventPayload(e.type().name(), letter, stats, meta);

        log.info("[TREE_LIVE] type={}, shareCode={}, subs={}",
                e.type(), e.shareCode(), eventBus.subscribers());

        eventBus.publish(e.shareCode(), payload);
    }

    private TreeStatsPayload buildStats(Tree tree) {
        long totalL = letterRepository.countByTreeAndStatus(tree, LetterStatus.APPROVED);
        long unreadL = letterRepository.countByTreeAndStatusAndIsReadFalse(tree, LetterStatus.APPROVED);
        return new TreeStatsPayload(Math.toIntExact(totalL), Math.toIntExact(unreadL));
    }

    private TreeLetterPayload buildLetterPayload(Long letterId) {
        if (letterId == null) return null;
        Letter l = letterRepository.findById(letterId).orElse(null);
        if (l == null) return new TreeLetterPayload(String.valueOf(letterId), null, null, null, true, null, null, null);

        return new TreeLetterPayload(
                String.valueOf(l.getId()),
                l.getWriter() == null ? null : String.valueOf(l.getWriter().getId()),
                l.getNickname(),
                l.getContent(),
                l.getIsRead(),
                l.getPositionX(),
                l.getPositionY(),
                String.valueOf(l.getCreatedAt())
        );
    }
}
