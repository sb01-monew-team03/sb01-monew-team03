package team03.monew.dto.common;

import java.time.Instant;
import java.util.List;

public record CursorPageResponse<T> (
    List<T> content,
    String nextCursor,
    Instant nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
