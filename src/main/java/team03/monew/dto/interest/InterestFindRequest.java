package team03.monew.dto.interest;

import jakarta.validation.constraints.NotBlank;

public record InterestFindRequest(
    String keyword,     // 검색어
    @NotBlank
    String orderBy,     // 정렬 속성 이름
    @NotBlank
    String direction,   // 정렬 방향
    String cursor,      // 커서 값(name 또는 subscriberCount)
    String after,       // 보조 커서(create_at)
    @NotBlank
    Integer limit,      // 커서 페이지 크기
    @NotBlank
    String monewRequestUserId   // 요청자 id - 구독 확인용
) {

}