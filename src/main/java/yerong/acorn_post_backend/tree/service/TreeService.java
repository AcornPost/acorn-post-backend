package yerong.acorn_post_backend.tree.service;

import yerong.acorn_post_backend.member.domain.Member;
import yerong.acorn_post_backend.tree.dto.TreeCreateRequest;
import yerong.acorn_post_backend.tree.dto.TreePublicResponse;
import yerong.acorn_post_backend.tree.dto.TreeResponse;
import yerong.acorn_post_backend.tree.dto.TreeSettingsRequest;

public interface TreeService {
    TreeResponse createTree(Long memberId, TreeCreateRequest request);
    TreeResponse getMyTree(Long memberId);
    TreePublicResponse getTreeByShareCode(String shareCode, Long currentMemberId);    void updateTreeSettings(Long memberId, TreeSettingsRequest request);
    void deleteTree(Long memberId);
    void updateTreeName(Long memberId, String treeName);
}
