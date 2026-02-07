package yerong.acorn_post_backend.tree.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yerong.acorn_post_backend.common.response.ApiResponse;
import yerong.acorn_post_backend.common.response.SuccessCode;
import yerong.acorn_post_backend.common.security.CurrentMemberIdResolver;
import yerong.acorn_post_backend.tree.dto.LetterRequest;
import yerong.acorn_post_backend.tree.dto.LetterResponse;
import yerong.acorn_post_backend.tree.dto.TreeInfoResponse;
import yerong.acorn_post_backend.tree.service.LetterService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/letters")
public class LetterApiController {

    private final LetterService letterService;
    private final CurrentMemberIdResolver currentMemberIdResolver;

    @GetMapping
    public ApiResponse<List<LetterResponse>> getAllLetters(
            @RequestParam String shareCode
    ) {
        Long memberId = currentMemberIdResolver.getOrNull();
        List<LetterResponse> letters = letterService.getAllLettersByShareCode(shareCode, memberId);
        return ApiResponse.success(SuccessCode.LETTERS_FETCHED, letters);
    }

    @GetMapping("/tree-info")
    public ApiResponse<TreeInfoResponse> getTreeInfo(
            @RequestParam String shareCode
    ) {
        TreeInfoResponse result = letterService.getTreeInfo(shareCode);
        return ApiResponse.success(SuccessCode.TREE_INFO_FETCHED, result);
    }

    @PostMapping
    public ApiResponse<LetterResponse> createLetter(
            @RequestParam String shareCode,
            @Valid @RequestBody LetterRequest request,
            HttpServletRequest httpRequest
    ) {
        Long memberId = currentMemberIdResolver.get();
        LetterResponse result = letterService.createLetter(
                shareCode,
                request,
                memberId,
                httpRequest
        );
        return ApiResponse.success(SuccessCode.LETTER_CREATED, result);
    }

    @GetMapping("/{id}")
    public ApiResponse<LetterResponse> readLetter(
            @PathVariable Long id
    ) {
        Long memberId = currentMemberIdResolver.getOrNull();
        LetterResponse result = letterService.readLetter(id, memberId);
        return ApiResponse.success(SuccessCode.LETTER_FETCHED, result);
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> updateLetter(
            @PathVariable Long id,
            @RequestBody LetterRequest request
    ) {
        Long memberId = currentMemberIdResolver.get();
        letterService.updateLetter(id, request, memberId);
        return ApiResponse.success(SuccessCode.LETTER_UPDATED, null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLetter(
            @PathVariable Long id
    ) {
        Long memberId = currentMemberIdResolver.get();
        letterService.deleteLetter(id, memberId);
        return ApiResponse.success(SuccessCode.LETTER_DELETED, null);
    }

    @GetMapping("/pending")
    public ApiResponse<List<LetterResponse>> getPendingLetters() {
        Long memberId = currentMemberIdResolver.get();
        List<LetterResponse> letters = letterService.getPendingLetters(memberId);
        return ApiResponse.success(SuccessCode.LETTERS_FETCHED, letters);
    }

    @PatchMapping("/{id}/approve")
    public ApiResponse<Void> approveLetter(
            @PathVariable Long id,
            @RequestParam boolean approve
    ) {
        Long memberId = currentMemberIdResolver.get();
        letterService.approveLetter(id, approve, memberId);
        return ApiResponse.success(SuccessCode.LETTER_APPROVED, null);
    }

    @PatchMapping("/{id}/report")
    public ApiResponse<Void> reportLetter(
            @PathVariable Long id
    ) {
        Long memberId = currentMemberIdResolver.get();
        letterService.reportLetter(id, memberId);
        return ApiResponse.success(SuccessCode.LETTER_REPORTED, null);
    }
}