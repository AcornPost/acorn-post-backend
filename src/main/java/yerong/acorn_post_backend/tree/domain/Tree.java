package yerong.acorn_post_backend.tree.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yerong.acorn_post_backend.common.domain.BaseTimeEntity;
import yerong.acorn_post_backend.member.domain.Member;

@Entity
@Table(name = "trees")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tree extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tree_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 12)
    private String shareCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean allowAnonymous = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean requireApproval = false;

    @OneToMany(mappedBy = "tree", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Letter> letters = new ArrayList<>();

    public void updateSettings(Boolean allowAnonymous, Boolean requireApproval) {
        if (allowAnonymous != null) {
            this.allowAnonymous = allowAnonymous;
        }
        if (requireApproval != null) {
            this.requireApproval = requireApproval;
        }
    }

    public boolean isOwner(Member member) {
        return this.owner.getId().equals(member.getId());
    }

    public void updateTreeName(String treeName) {
        this.title = treeName;
    }
}
