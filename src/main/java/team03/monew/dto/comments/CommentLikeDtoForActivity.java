package team03.monew.dto.comments;

import java.time.Instant;
import java.util.UUID;

public record CommentLikeDtoForActivity(
    UUID id,
    Instant createdAt,
    UUID commentId,
    UUID articleId,
    String articleTitle,
    UUID commentUserId,
    String commentUserNickname,
    String commentContent,
    int commentLikeCount,
    Instant commentCreatedAt
) {

}
