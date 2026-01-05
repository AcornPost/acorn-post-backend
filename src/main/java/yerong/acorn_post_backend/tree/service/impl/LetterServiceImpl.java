package yerong.acorn_post_backend.tree.service.impl;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yerong.acorn_post_backend.common.response.ApiException;
import yerong.acorn_post_backend.common.response.ErrorCode;
import yerong.acorn_post_backend.tree.domain.Letter;
import yerong.acorn_post_backend.tree.dto.LetterRequest;
import yerong.acorn_post_backend.tree.dto.LetterResponse;
import yerong.acorn_post_backend.tree.dto.TreeInfoResponse;
import yerong.acorn_post_backend.tree.repository.LetterRepository;
import yerong.acorn_post_backend.tree.service.LetterService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LetterServiceImpl implements LetterService {
    private final LetterRepository letterRepository;
    private final Random random = new Random();

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
    public List<LetterResponse> getAllLetters() {
        return letterRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(LetterResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public TreeInfoResponse getTreeInfo() {
        long totalLetters = letterRepository.count();
        long unreadCount = letterRepository.countByIsReadFalse();
        return TreeInfoResponse.of(totalLetters, unreadCount);
    }

    @Override
    @Transactional
    public LetterResponse createLetter(LetterRequest letterRequest) {
        LetterResponse.Position position = generateSafeTreePosition();
        Letter letter = letterRequest.toEntity(position.getX(), position.getY());
        Letter savedLetter = letterRepository.save(letter);
        return LetterResponse.from(savedLetter);
    }

    @Override
    @Transactional
    public LetterResponse readLetter(Long id) {
        Letter letter = findLetterById(id);
        letter.markAsRead();
        return LetterResponse.from(letter);
    }

    @Override
    @Transactional
    public void deleteLetter(Long id) {
        Letter letter = findLetterById(id);
        letterRepository.delete(letter);
    }

    @Override
    @Transactional
    public void updateLetter(Long id, LetterRequest request) {
        Letter letter = findLetterById(id);
        letter.update(request.getNickname(), request.getContent());
    }

    private Letter findLetterById(Long id) {
        return letterRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.LETTER_NOT_FOUND));
    }
}
