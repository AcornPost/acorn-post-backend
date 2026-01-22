package yerong.acorn_post_backend.tree.service.impl;

import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.member.repository.MemberRepository;
import yerong.acorn_post_backend.tree.domain.LetterStatus;
import yerong.acorn_post_backend.tree.domain.Tree;
import yerong.acorn_post_backend.tree.dto.TreeCreateRequest;
import yerong.acorn_post_backend.tree.dto.TreePublicResponse;
import yerong.acorn_post_backend.tree.dto.TreeResponse;
import yerong.acorn_post_backend.tree.dto.TreeSettingsRequest;
import yerong.acorn_post_backend.tree.realtime.event.TreeDomainEvent;
import yerong.acorn_post_backend.tree.realtime.event.TreeEventType;
import yerong.acorn_post_backend.tree.repository.LetterRepository;
import yerong.acorn_post_backend.tree.repository.TreeRepository;
import yerong.acorn_post_backend.tree.service.TreeService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TreeServiceImpl implements TreeService {
    private final TreeRepository treeRepository;
    private final LetterRepository letterRepository;
    private static final String SHARE_CODE_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHARE_CODE_LENGTH = 12;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public TreeResponse createTree(Long memberId, TreeCreateRequest request) {
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        if(treeRepository.findByOwner(owner).isPresent()) {
            throw new ApiException(ErrorCode.TREE_ALREADY_EXISTS);
        }

        String shareCode = generateUniqueShareCode();
        
        Tree tree = Tree.builder()
                .shareCode(shareCode)
                .owner(owner)
                .title(request.getTitle())
                .description(request.getDescription())
                .allowAnonymous(true)
                .requireApproval(false)
                .build();
        
        Tree savedTree = treeRepository.save(tree);
        return TreeResponse.from(savedTree, 0L, 0L, 0L);
    }

    @Override
    public TreeResponse getMyTree(Long memberId) {
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        Tree tree = treeRepository.findByOwner(owner).orElseThrow(() -> new ApiException(ErrorCode.TREE_NOT_FOUND));
        long totalLetters = letterRepository.countByTreeAndStatus(tree, LetterStatus.APPROVED);
        long unreadCount = letterRepository.countByTreeAndIsReadFalse(tree);
        long pendingCount = letterRepository.countByTreeAndStatus(tree, LetterStatus.PENDING);

        return TreeResponse.from(tree, totalLetters, unreadCount, pendingCount);
    }

    @Override
    public TreePublicResponse getTreeByShareCode(String shareCode, Long currentMemberId) {
        Tree tree = findTreeByShareCode(shareCode);
        long totalLetters = letterRepository.countByTreeAndStatus(tree, LetterStatus.APPROVED);
        boolean isOwner = false;
        if (currentMemberId != null) {
            isOwner = tree.getOwner().getId().equals(currentMemberId);
        }
        return TreePublicResponse.from(tree, totalLetters, isOwner);
    }

    @Override
    @Transactional
    public void updateTreeSettings(Long memberId, TreeSettingsRequest request) {
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        Tree tree = treeRepository.findByOwner(owner).orElseThrow(() -> new ApiException(ErrorCode.TREE_NOT_FOUND));
        tree.updateSettings(request.getAllowAnonymous(), request.getRequireApproval());
    }

    @Override
    @Transactional
    public void deleteTree(Long memberId) {
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        Tree tree = treeRepository.findByOwner(owner)
                .orElseThrow(() -> new ApiException(ErrorCode.TREE_NOT_FOUND));

        treeRepository.delete(tree);
    }

    @Override
    @Transactional
    public void updateTreeName(Long memberId, String treeName) {
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        Tree tree = treeRepository.findByOwner(owner)
                .orElseThrow(() -> new ApiException(ErrorCode.TREE_NOT_FOUND));

        tree.updateTreeName(treeName);
        eventPublisher.publishEvent(new TreeDomainEvent(
                tree.getShareCode(),
                TreeEventType.TREE_TITLE_UPDATED,
                null
        ));
    }

    private String generateUniqueShareCode() {
        SecureRandom random = new SecureRandom();
        String shareCode;

        do {
            StringBuilder sb = new StringBuilder(SHARE_CODE_LENGTH);
            for (int i = 0; i < SHARE_CODE_LENGTH; i++) {
                sb.append(SHARE_CODE_CHARS.charAt(random.nextInt(SHARE_CODE_CHARS.length())));
            }
            shareCode = sb.toString();
        } while (treeRepository.existsByShareCode(shareCode));

        return shareCode;
    }

    private Tree findTreeByShareCode(String shareCode) {
        return treeRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new ApiException(ErrorCode.TREE_NOT_FOUND));
    }
}
