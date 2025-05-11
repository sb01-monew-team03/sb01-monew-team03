package team03.monew.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public record ArticleFindRequest(
    String keyword,                    // 검색어 (제목, 요약)
    UUID interestId,                  // 관심사 ID
    List<String> sourceIn,            // 출처 목록 (NAVER 등)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    Instant publishDateFrom,          // 시작 날짜

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    Instant publishDateTo,            // 종료 날짜

    @NotBlank(message = "orderBy는 필수입니다.")
    String orderBy,                   // 정렬 기준 (publishDate, commentCount, viewCount)

    @NotBlank(message = "direction은 필수입니다.")
    String direction,                 // 정렬 방향 (ASC, DESC)

    String cursor,                    // 커서 값

    String after,                    // 보조 커서 (createdAt)

    @NotNull(message = "limit은 필수입니다.")
    Integer limit                     // 페이지 크기
) {

    // limit 문제를 해결하기 위한 임시방편
    public ArticleFindRequest {
        limit = 50;
    }

}
