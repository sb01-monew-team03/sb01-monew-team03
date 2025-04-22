package team03.monew.entity.comments;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team03.monew.entity.article.Article;
import team03.monew.entity.base.BaseDeletableEntity;
import team03.monew.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import team03.monew.entity.article.Article;
import team03.monew.entity.user.User;

/**
 * 뉴스 기사 댓글 엔티티
 * - BaseDeletableEntity를 상속받아 삭제 기능 구현
 * - 생성일(createdAt), 수정일(updatedAt), 삭제일(deletedAt) 자동 관리
 */
@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseDeletableEntity {

    // 댓글 내용 (최대 500자)
    @Column(nullable = false, length = 500)
    private String content;

    // 좋아요 수 (기본값 0)
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer likeCount = 0;

    // 작성자 정보 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 소속 기사 정보 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    // 좋아요 목록 (일대다 관계)
    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    /**
     * 댓글 생성자
     * @param content 댓글 내용
     * @param user    작성자
     * @param article 소속 기사
     */
    public Comment(String content, User user, Article article) {
        this.content = content;
        this.user = user;
        this.article = article;
    }


    // 댓글 내용 업데이트
    public void update(String newContent) {
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
        }
    }


    // 좋아요 수 증가
    public void increaseLikeCount() {
        this.likeCount++;
    }

    //좋아요 수 감소 (0 이하로는 감소하지 않음)
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }


    @Override
    public void delete() {
        super.delete();
        if (likes != null) {
            likes.forEach(like -> like.getComment().getLikes().remove(like));
        }
    }
}