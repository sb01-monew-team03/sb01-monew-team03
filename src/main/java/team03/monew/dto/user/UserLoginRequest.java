package team03.monew.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
    @NotBlank(message = "이메일은 필수입니다")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    String password
) {

}