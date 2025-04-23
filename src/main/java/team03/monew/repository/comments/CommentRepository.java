package team03.monew.repository.comments;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team03.monew.entity.comments.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // 논리 삭제되지 않은 댓글만 조회, 페이징 및 정렬 지원
    Page<Comment> findByArticleIdAndDeletedAtIsNull(UUID articleId, Pageable pageable);

    // 작성자 본인 댓글 확인용
    Optional<Comment> findByIdAndUserId(UUID commentId, UUID userId);
}