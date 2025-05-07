package team03.monew.dto.interest;

import java.time.Instant;
import java.util.List;

// 다음 페이지에 필요한 정보
public record PaginationDto(
    List<InterestDto> content,
    String nextCursor,
    Instant nextAfter,
    boolean hasNext,
    int size
) {

}
