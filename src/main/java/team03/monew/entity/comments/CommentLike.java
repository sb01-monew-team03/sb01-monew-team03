package team03.monew.entity.comments;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import team03.monew.entity.article.Article;

/**
 * 사용자가 댓글에 누른 좋아요를 나타내는 엔티티 클래스.
 * <p>테이블 매핑: comment_likes
 */
@Entity
@Table(name = "comment_likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike {

    /**
     * 좋아요의 고유 식별자.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 좋아요가 속한 댓글. 다대일 관계.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    /**
     * 좋아요를 누른 사용자. 다대일 관계.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 댓글이 속한 기사. 다대일 관계.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    /**
     * 좋아요 생성 시간. 수정 불가능.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 좋아요 생성자.
     *
     * @param comment 좋아요 대상 댓글
     * @param user 좋아요를 누른 사용자
     * @param article 댓글이 속한 기사
     */
    public CommentLike(Comment comment, User user, Article article) {
        this.comment = comment;
        this.user = user;
        this.article = article;
    }
}