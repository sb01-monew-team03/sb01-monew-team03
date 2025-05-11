package team03.monew.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 100, message = "닉네임은 2자 이상, 100자 이하여야 합니다")
    String nickname
) {

}
