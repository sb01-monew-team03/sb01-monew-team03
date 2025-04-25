package team03.monew.dto.interest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record InterestRegisterRequest(
    @NotBlank(message = "관심사를 입력해주세요.")
    @Size(max = 50, message = "관심사는 50자 이하로 입력해주세요.")
    String name,

    @NotNull
    List<@NotBlank(message = "키워드를 입력해주세요.") @Size(max = 20, message = "키워드는 20자 이하로 입력해주세요.") String> keywords
) {

}