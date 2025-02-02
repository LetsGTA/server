package net.letsgta.server.api.login.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "이메일은 필수 입력 사항입니다.")
        @Email(message = "입력 값이 이메일 형식이어야 합니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
        String password
) {
}
