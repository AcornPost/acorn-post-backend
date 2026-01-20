package yerong.acorn_post_backend.tree.domain;

public enum LetterStatus {
    PENDING("승인 대기"),
    APPROVED("승인됨"),
    REJECTED("거부됨"),
    REPORTED("신고됨");

    private final String description;

    LetterStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
