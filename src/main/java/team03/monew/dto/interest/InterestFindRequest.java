package team03.monew.dto.interest;

import jakarta.validation.constraints.NotBlank;

public record InterestFindRequest(
    String keyword,
    @NotBlank String orderBy,
    @NotBlank String direction,
    String cursor,
    String after,
    @NotBlank Integer limit,
    @NotBlank String monewRequestUserId
) {

}