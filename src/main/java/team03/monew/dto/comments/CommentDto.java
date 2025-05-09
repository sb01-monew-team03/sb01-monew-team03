package team03.monew.dto.comments;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(
        UUID id,
        UUID articleId,
        UUID userId,
        String userNickname,
        String content,
        long likeCount,
        boolean likedByMe,
        Instant createdAt
) {}
