package team03.monew.dto.comments;

import java.time.Instant;
import java.util.List;


public record CommentCursorPageResponse<T>(
        List<T> content,
        String nextCursor,
        Instant nextAfter,
        int size,
        long totalElements,
        boolean hasNext
) {}
