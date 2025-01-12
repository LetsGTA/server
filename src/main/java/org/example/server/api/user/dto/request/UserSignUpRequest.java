package org.example.server.api.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.example.server.api.user.enums.RoleName;
import org.example.server.api.user.entity.User;

public record UserSignUpRequest(

        @NotBlank(message = "비밀번호는 공백이 불가합니다.")
        String password,

        @NotBlank(message = "이메일은 공백이 불가합니다.")
        String email,

        @Size(max = 20, message = "닉네임의 길이는 최대 20자 입니다.")
        @NotBlank(message = "닉네임은 공백이 불가합니다.")
        String nickname,

        @NotBlank(message = "이메일 인증 토큰이 필요합니다.")
        String token
) {
    public User to() {
        return User.builder()
                .password(password)
                .email(email)
                .nickname(nickname)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(RoleName.ROLE_USER)
                .build();
    }
}
