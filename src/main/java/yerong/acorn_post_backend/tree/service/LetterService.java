package yerong.acorn_post_backend.tree.service;

import java.util.List;
import yerong.acorn_post_backend.tree.dto.LetterRequest;
import yerong.acorn_post_backend.tree.dto.LetterResponse;
import yerong.acorn_post_backend.tree.dto.TreeInfoResponse;

public interface LetterService {
    List<LetterResponse> getAllLetters();
    TreeInfoResponse getTreeInfo();
    LetterResponse createLetter(LetterRequest letterRequest);
    LetterResponse readLetter(Long id);
    void deleteLetter(Long id);
    void updateLetter(Long id, LetterRequest request);
}
