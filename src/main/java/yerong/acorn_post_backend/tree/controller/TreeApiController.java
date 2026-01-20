package yerong.acorn_post_backend.tree.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yerong.acorn_post_backend.common.response.ApiResponse;
import yerong.acorn_post_backend.common.response.SuccessCode;
import yerong.acorn_post_backend.common.security.CurrentMemberIdResolver;
import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.tree.dto.TreeCreateRequest;
import yerong.acorn_post_backend.tree.dto.TreePublicResponse;
import yerong.acorn_post_backend.tree.dto.TreeResponse;
import yerong.acorn_post_backend.tree.dto.TreeSettingsRequest;
import yerong.acorn_post_backend.tree.dto.UpdateTreeNameRquest;
import yerong.acorn_post_backend.tree.service.TreeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trees")
public class TreeApiController {

    private final TreeService treeService;
    private final CurrentMemberIdResolver currentMemberIdResolver;
    @PostMapping
    public ApiResponse<TreeResponse> createTree(
            @RequestBody TreeCreateRequest request
    ) {
        Long memberId = currentMemberIdResolver.get();
        TreeResponse result = treeService.createTree(memberId, request);
        return ApiResponse.success(SuccessCode.TREE_CREATED, result);
    }

    @GetMapping("/my")
    public ApiResponse<TreeResponse> getMyTree() {
        Long memberId = currentMemberIdResolver.get();
        TreeResponse result = treeService.getMyTree(memberId);
        return ApiResponse.success(SuccessCode.TREE_INFO_FETCHED, result);
    }

    @GetMapping("/public/{shareCode}")
    public ApiResponse<TreePublicResponse> getPublicTree(
            @PathVariable String shareCode
    ) {
        Long currentMemberId = currentMemberIdResolver.getOrNull();
        TreePublicResponse result = treeService.getTreeByShareCode(shareCode, currentMemberId);
        return ApiResponse.success(SuccessCode.TREE_INFO_FETCHED, result);
    }

    @PatchMapping("/settings")
    public ApiResponse<Void> updateTreeSettings(
            @RequestBody TreeSettingsRequest request
    ) {
        Long memberId = currentMemberIdResolver.get();
        treeService.updateTreeSettings(memberId, request);
        return ApiResponse.success(SuccessCode.TREE_UPDATED);
    }

    @DeleteMapping
    public ApiResponse<Void> deleteTree() {
        Long memberId = currentMemberIdResolver.get();
        treeService.deleteTree(memberId);
        return ApiResponse.success(SuccessCode.TREE_DELETED);
    }

    @PatchMapping("/tree-name")
    public ApiResponse<Void> updateTreeName(@RequestBody UpdateTreeNameRquest request) {
        Long memberId = currentMemberIdResolver.get();
        treeService.updateTreeName(memberId, request.name());
        return ApiResponse.success(SuccessCode.TREE_UPDATED);
    }
}