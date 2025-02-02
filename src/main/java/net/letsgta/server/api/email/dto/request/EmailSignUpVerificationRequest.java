package net.letsgta.server.api.email.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailSignUpVerificationRequest(

        @NotBlank(message = "토큰이 존재하지 않습니다.")
        String token,

        @NotBlank(message = "인증 번호가 존재하지 않습니다.")
        String verificationNumber
) {
}
