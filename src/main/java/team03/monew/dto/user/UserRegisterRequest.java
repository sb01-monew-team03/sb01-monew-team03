package team03.monew.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// 사용자 등록 request
public record UserRegisterRequest(

    @NotBlank(message = "이메일은 필수입니다")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다")
    @Email(message = "유효한 이메일 형식이어야 합니다")
    String email,

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 100, message = "닉네임은 2자 이상, 100자 이하여야 합니다")
    String nickname,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상 100자 이하여야 합니다")
    String password
) {

}