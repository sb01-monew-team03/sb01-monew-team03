package team03.monew.repository.comments;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;
import team03.monew.entity.user.User;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);

    Optional<CommentLike> findByCommentIdAndUserId(UUID commentId, UUID userId);

    boolean existsByCommentAndUser(Comment comment, User user);

    List<CommentLike> findTop10ByUserOrderByCreatedAtDesc(User user);
}