package yerong.acorn_post_backend.tree.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.member.repository.MemberRepository;
import yerong.acorn_post_backend.tree.domain.Letter;
import yerong.acorn_post_backend.tree.domain.LetterStatus;
import yerong.acorn_post_backend.tree.domain.Tree;
import yerong.acorn_post_backend.tree.dto.LetterRequest;
import yerong.acorn_post_backend.tree.dto.LetterResponse;
import yerong.acorn_post_backend.tree.dto.TreeInfoResponse;
import yerong.acorn_post_backend.tree.repository.LetterRepository;
import yerong.acorn_post_backend.tree.repository.TreeRepository;
import yerong.acorn_post_backend.tree.service.LetterService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LetterServiceImpl implements LetterService {
    private final LetterRepository letterRepository;
    private final Random random = new Random();
    private final TreeRepository treeRepository;
    private final MemberRepository memberRepository;
    private LetterResponse.Position generateSafeTreePosition() {
        double[][] leafZones = {
                {50, 30, 15},
                {30, 45, 12},
                {70, 45, 12},
                {50, 55, 10},
                {20, 35, 8},
                {80, 35, 8}
        };

        double[] zone = leafZones[random.nextInt(leafZones.length)];
        double angle = random.nextDouble() * 2 * Math.PI;
        double radius = random.nextDouble() * zone[2];

        double x = zone[0] + radius * Math.cos(angle);
        double y = zone[1] + radius * Math.sin(angle);

        return new LetterResponse.Position(x, y);
    }

    @Override
    public List<LetterResponse> getAllLettersByShareCode(String shareCode, Long memberId) {
        Tree tree = findTreeByShareCode(shareCode);
        return letterRepository.findByTreeAndStatusOrderByCreatedAtDesc(tree, LetterStatus.APPROVED)
                .stream()
                .map(letter -> LetterResponse.from(letter, memberId))
                .collect(Collectors.toList());
    }

    @Override
    public TreeInfoResponse getTreeInfo(String shareCode) {
        Tree tree = findTreeByShareCode(shareCode);
        long totalLetters = letterRepository.countByTreeAndStatus(tree, LetterStatus.APPROVED);
        long unreadCount = letterRepository.countByTreeAndIsReadFalse(tree);
        return TreeInfoResponse.of(totalLetters, unreadCount);
    }

    @Override
    @Transactional
    public LetterResponse createLetter(
            String shareCode,
            LetterRequest request,
            Long memberId,
            HttpServletRequest httpRequest
    ) {
        Tree tree = findTreeByShareCode(shareCode);
        if (memberId == null) {
            throw new ApiException(ErrorCode.LOGIN_REQUIRED, "편지를 쓰려면 로그인이 필요해요!");
        }
        Member currentMember = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        if (currentMember == null && !tree.getAllowAnonymous()) {
            throw new ApiException(ErrorCode.LOGIN_REQUIRED);
        }
        if (request.nickname() == null || request.nickname().trim().isEmpty()) {
            throw new ApiException(ErrorCode.LETTER_INVALID_INPUT_NICKNAME);
        }
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new ApiException(ErrorCode.LETTER_INVALID_INPUT_CONTENT);
        }
        LetterResponse.Position position = generateSafeTreePosition();
        LetterStatus status = tree.getRequireApproval() ? LetterStatus.PENDING : LetterStatus.APPROVED;

        Letter letter = Letter.builder()
                .tree(tree)
                .writer(currentMember)
                .nickname(request.nickname())
                .content(request.content())
                .positionX(position.getX())
                .positionY(position.getY())
                .isAnonymous(currentMember == null)
                .status(status)
                .writerIp(currentMember == null ? getClientIp(httpRequest) : null)
                .isRead(false)
                .isMine(currentMember != null && tree.isOwner(currentMember))
                .build();

        Letter savedLetter = letterRepository.save(letter);
        return LetterResponse.from(savedLetter, memberId);
    }

    @Override
    @Transactional
    public LetterResponse readLetter(Long id, Long memberId) {
        Letter letter = findLetterById(id);
        Member currentMember = (memberId != null)
                ? memberRepository.findById(memberId).orElse(null)
                : null;
        if (letter.getStatus() != LetterStatus.APPROVED) {
            if (currentMember == null || !letter.getTree().isOwner(currentMember)) {
                throw new ApiException(ErrorCode.LETTER_NOT_FOUND);
            }
        }

        if (currentMember != null && letter.getTree().isOwner(currentMember)) {
            letter.markAsRead();
        }
        return LetterResponse.from(letter, memberId);
    }

    @Override
    @Transactional
    public void updateLetter(Long id, LetterRequest request, Long memberId) {
        Letter letter = findLetterById(id);

        Member currentMember = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        if (request.nickname() == null || request.nickname().trim().isEmpty()) {
            throw new ApiException(ErrorCode.LETTER_INVALID_INPUT_NICKNAME);
        }
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new ApiException(ErrorCode.LETTER_INVALID_INPUT_CONTENT);
        }
        validateLetterOwnership(letter, currentMember);
        letter.update(request.nickname(), request.content());
    }

    @Override
    @Transactional
    public void deleteLetter(Long id, Long memberId) {
        Letter letter = findLetterById(id);
        Member currentMember = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        if(letter.getWriter() != null) {
            if(!letter.getWriter().getId().equals(currentMember.getId()) && !letter.getTree().isOwner(currentMember)) {
                throw new ApiException(ErrorCode.FORBIDDEN);
            }
        } else if(!letter.getTree().isOwner(currentMember)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
        letterRepository.delete(letter);
    }

    @Override
    @Transactional
    public void approveLetter(Long id, boolean approve, Long memberId) {
        Letter letter = findLetterById(id);
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        validateTreeOwner(letter.getTree(), owner);
        if(letter.getStatus() != LetterStatus.PENDING) {
            throw new ApiException(ErrorCode.LETTER_ALREADY_PROCESSED);
        }
        letter.updateStatus(approve ? LetterStatus.APPROVED : LetterStatus.REJECTED);
    }

    @Override
    @Transactional
    public void reportLetter(Long id, Long memberId) {
        Letter letter = findLetterById(id);
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        validateTreeOwner(letter.getTree(), owner);

        letter.updateStatus(LetterStatus.REPORTED);
    }

    @Override
    public List<LetterResponse> getPendingLetters(Long memberId) {
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new ApiException(
                ErrorCode.MEMBER_NOT_FOUND
        ));
        Tree tree = treeRepository.findByOwner(owner)
                .orElseThrow(() -> new ApiException(ErrorCode.TREE_NOT_FOUND));

        return letterRepository.findByTreeAndStatusOrderByCreatedAtDesc(tree, LetterStatus.PENDING)
                .stream()
                .map(letter -> LetterResponse.from(letter, memberId))
                .collect(Collectors.toList());
    }

    private Letter findLetterById(Long id) {
        return letterRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.LETTER_NOT_FOUND));
    }

    private Tree findTreeByShareCode(String shareCode) {
        return treeRepository.findByShareCode(shareCode)
                .orElseThrow(() -> new ApiException(ErrorCode.TREE_NOT_FOUND));
    }

    private void validateLetterOwnership(Letter letter, Member currentMember) {
        if(letter.getWriter() == null || !letter.getWriter().getId().equals(currentMember.getId())) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateTreeOwner(Tree tree, Member owner) {
        if(!tree.isOwner(owner)) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }
    }
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
