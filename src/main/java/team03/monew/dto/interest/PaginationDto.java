package team03.monew.dto.interest;

import java.time.Instant;

// 다음 페이지에 필요한 정보
public record PaginationDto(
    String nextCursor,
    Instant nextAfter,
    boolean hasNext,
    int size
) {

}
