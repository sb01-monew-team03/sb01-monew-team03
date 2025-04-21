package team03.monew.entity.comments;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.monew.entity.article.Article;
import team03.monew.entity.base.BaseEntity;
import team03.monew.entity.user.User;

/**
 * 댓글 좋아요 엔티티
 * - BaseEntity를 상속받아 기본 필드(id, createdAt) 자동 관리
 */
@Entity
@Table(name = "comment_likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike extends BaseEntity {

    // 좋아요 대상 댓글 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    // 좋아요 누른 사용자 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 댓글이 속한 기사 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    /**
     * 좋아요 생성자
     * @param comment 대상 댓글
     * @param user 좋아요 누른 사용자
     * @param article 소속 기사
     */
    public CommentLike(Comment comment, User user, Article article) {
        this.comment = comment;
        this.user = user;
        this.article = article;
    }
}