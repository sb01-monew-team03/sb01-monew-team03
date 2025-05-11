package team03.monew.dto.comments;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CommentActivityDto(
    UUID id,
    UUID articleId,
    String articleTitle,
    UUID userId,
    String userNickname,
    String content,
    long likeCount,
    Instant createdAt
) {

}
