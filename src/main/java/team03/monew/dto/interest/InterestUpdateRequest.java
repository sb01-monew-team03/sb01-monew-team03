package team03.monew.dto.interest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record InterestUpdateRequest(
    @NotNull
    List<@Size(max = 20, message = "키워드는 20자 이하로 입력해주세요.") String> keywords
) {

}