package yerong.acorn_post_backend.tree.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yerong.acorn_post_backend.common.response.ApiResponse;
import yerong.acorn_post_backend.common.response.SuccessCode;
import yerong.acorn_post_backend.tree.domain.Letter;
import yerong.acorn_post_backend.tree.dto.LetterRequest;
import yerong.acorn_post_backend.tree.dto.LetterResponse;
import yerong.acorn_post_backend.tree.dto.TreeInfoResponse;
import yerong.acorn_post_backend.tree.service.LetterService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/letters")
public class LetterApiController {

    private final LetterService letterService;

    @GetMapping
    public ApiResponse<List<LetterResponse>> getAllLetters() {
        List<LetterResponse> letters = letterService.getAllLetters();
        return ApiResponse.success(SuccessCode.LETTERS_FETCHED, letters);
    }

    @GetMapping("/tree-info")
    public ApiResponse<TreeInfoResponse> getTreeInfo() {
        TreeInfoResponse result = letterService.getTreeInfo();
        return ApiResponse.success(SuccessCode.TREE_INFO_FETCHED, result);
    }

    @PostMapping
    public ApiResponse<LetterResponse> createLetter(@RequestBody LetterRequest request) {
        LetterResponse result = letterService.createLetter(request);
        return ApiResponse.success(SuccessCode.LETTER_CREATED, result);
    }

    @GetMapping("/{id}")
    public ApiResponse<LetterResponse> readLetter(@PathVariable Long id) {
        LetterResponse result = letterService.readLetter(id);
        return ApiResponse.success(SuccessCode.LETTER_FETCHED, result);
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> updateLetter(
            @PathVariable Long id,
            @RequestBody LetterRequest request
    ) {
        letterService.updateLetter(id, request);
        return ApiResponse.success(SuccessCode.LETTER_UPDATED, null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLetter(@PathVariable Long id) {
        letterService.deleteLetter(id);
        return ApiResponse.success(SuccessCode.LETTER_DELETED, null);
    }
}
