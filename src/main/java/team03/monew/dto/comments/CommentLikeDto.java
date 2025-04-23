package team03.monew.dto.comments;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentLikeDto(
        UUID id,
        UUID likedBy,
        Instant createdAt,          // ← Instant
        UUID commentId,
        UUID articleId,
        UUID commentUserId,
        String commentUserNickname,
        String commentContent,
        long commentLikeCount,
        Instant commentCreatedAt    // ← Instant
) {}
