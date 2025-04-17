package team03.monew.dto.comments.data;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentLikeDto(
        UUID id,
        UUID likedBy,
        LocalDateTime createdAt,
        UUID commentId,
        UUID articleId,
        UUID commentUserId,
        String commentUserNickname,
        String commentContent,
        long commentLikeCount,
        LocalDateTime commentCreatedAt
) {}
