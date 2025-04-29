package team03.monew.repository.comments;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team03.monew.entity.article.Article;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.user.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // 논리 삭제되지 않은 댓글만 조회, 페이징 및 정렬 지원
    Page<Comment> findByArticleIdAndDeletedAtIsNull(UUID articleId, Pageable pageable);

    // 작성자 본인 댓글 확인용
    Optional<Comment> findByIdAndUserId(UUID commentId, UUID userId);

    // 활동 관리를 위한 최근 작성한 댓글(최대 10건) 추출
    @EntityGraph(attributePaths = {"article"})
    List<Comment> findTop10ByUserOrderByCreatedAtDesc(User user);

    Long countByArticle(Article article);
}