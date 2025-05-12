package team03.monew.dto.interest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InterestFindRequest(
    String keyword,     // 검색어
    @NotBlank(message = "orderBy는 필수입니다.")
    String orderBy,     // 정렬 속성 이름
    @NotBlank(message = "direction은 필수입니다,")
    String direction,   // 정렬 방향
    String cursor,      // 커서 값(name 또는 subscriberCount)
    String after,       // 보조 커서(create_at)
    @NotNull(message = "limit은 필수입니다.")
    Integer limit      // 커서 페이지 크기
) {

}