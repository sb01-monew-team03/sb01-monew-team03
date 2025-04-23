package team03.monew.mapper.comments;

import org.springframework.stereotype.Component;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;


@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment, boolean likedByMe) {
        return new CommentDto(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getLikeCount().longValue(),
                likedByMe,
                comment.getCreatedAt()  // 필요 타입: LocalDateTime
        );
    }

    public CommentDto toDto(Comment comment) {
        return toDto(comment, false);
    }

    public CommentLikeDto toLikeDto(CommentLike like) {
        Comment comment = like.getComment();
        return new CommentLikeDto(
                like.getId(),
                like.getUser().getId(),
                like.getCreatedAt(),       // Instant 타입
                comment.getId(),
                like.getArticle().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getLikeCount().longValue(),
                comment.getCreatedAt()     // Instant 타입
        );
    }
}
