package team03.monew.repository.comment;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import team03.monew.entity.comments.Comment;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

}
