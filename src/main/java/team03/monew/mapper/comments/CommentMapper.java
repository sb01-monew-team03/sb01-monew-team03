package team03.monew.mapper.comments;

import org.springframework.stereotype.Component;
import team03.monew.dto.comments.CommentDto;
import team03.monew.dto.comments.CommentLikeDto;
import team03.monew.entity.comments.Comment;
import team03.monew.entity.comments.CommentLike;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class CommentMapper {

    private static final ZoneId ZONE = ZoneId.systemDefault(); // or ZoneId.of("Asia/Seoul");

    public CommentDto toDto(Comment comment, boolean likedByMe) {
        LocalDateTime createdAt = LocalDateTime.ofInstant(comment.getCreatedAt(), ZONE);

        return new CommentDto(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getLikeCount().longValue(),
                likedByMe,
                createdAt
        );
    }

    public CommentDto toDto(Comment comment) {
        return toDto(comment, false);
    }

    public CommentLikeDto toLikeDto(CommentLike like) {
        var comment = like.getComment();
        LocalDateTime likeCreatedAt    = LocalDateTime.ofInstant(like.getCreatedAt(), ZONE);
        LocalDateTime commentCreatedAt = LocalDateTime.ofInstant(comment.getCreatedAt(), ZONE);

        return new CommentLikeDto(
                like.getId(),
                like.getUser().getId(),
                likeCreatedAt,
                comment.getId(),
                like.getArticle().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getLikeCount().longValue(),
                commentCreatedAt
        );
    }
}
