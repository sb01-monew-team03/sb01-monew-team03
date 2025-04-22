package team03.monew.entity.comments;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;
import team03.monew.entity.article.Article;
import team03.monew.entity.user.User;

/**
 * 뉴스 기사에 대한 댓글을 나타내는 엔티티 클래스.
 * 논리 삭제 기능을 지원하며, 좋아요 기능과 연관 관계를 가집니다.
 * <p>테이블 매핑: comments
 */
@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE comments SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Comment{

    /**
     * 댓글 고유 ID. 기본 키.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 댓글 내용. 최대 500자까지 허용.
     */
    @Column(nullable = false, length = 500)
    private String content;

    /**
     * 댓글의 좋아요 수. 기본값 0.
     */
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer likeCount = 0;

    /**
     * 논리 삭제 여부를 나타내는 플래그. 기본값 false.
     */
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean deleted = false;

    /**
     * 댓글을 작성한 사용자. 다대일 관계.
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
     * 이 댓글에 대한 좋아요 목록. 일대다 관계.
     */
    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    /**
     * 댓글 생성자.
     *
     * @param content 댓글 내용
     * @param user 댓글 작성자
     * @param article 댓글이 속한 기사
     */
    public Comment(String content, User user, Article article) {
        this.content = content;
        this.user = user;
        this.article = article;
    }

    /**
     * 댓글 내용을 업데이트합니다.
     *
     * @param newContent 새로운 댓글 내용 (null이 아니고 기존 내용과 다른 경우에만 업데이트)
     */
    public void update(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
    }

    /**
     * 댓글의 좋아요 수를 1 증가시킵니다.
     */
    public void increaseLikeCount() {
        this.likeCount++;
    }

    /**
     * 댓글의 좋아요 수를 1 감소시킵니다. (0 이하로는 감소하지 않음)
     */
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * 댓글을 논리적으로 삭제합니다. (deleted 플래그를 true로 설정)
     */
    public void delete() {
        this.deleted = true;
    }
}