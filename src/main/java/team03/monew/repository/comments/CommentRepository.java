package team03.monew.repository.comments;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team03.monew.entity.comments.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByArticleId(UUID articleId);

    Optional<Comment> findByIdAndUserId(UUID commentId, UUID userId);
}
