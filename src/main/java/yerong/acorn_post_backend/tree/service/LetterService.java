package yerong.acorn_post_backend.tree.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.tree.dto.LetterRequest;
import yerong.acorn_post_backend.tree.dto.LetterResponse;
import yerong.acorn_post_backend.tree.dto.TreeInfoResponse;

public interface LetterService {

    List<LetterResponse> getAllLettersByShareCode(String shareCode, Long memberId);
    TreeInfoResponse getTreeInfo(String shareCode);
    LetterResponse createLetter(
            String shareCode,
            LetterRequest request,
            Long memberId,
            HttpServletRequest httpRequest
    );
    LetterResponse readLetter(Long id, Long memberId);
    void updateLetter(Long id, LetterRequest request, Long memberId);
    void deleteLetter(Long id, Long memberId);
    void approveLetter(Long id, boolean approve, Long memberId);
    void reportLetter(Long id, Long memberId);
    List<LetterResponse> getPendingLetters(Long memberId);
}