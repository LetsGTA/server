package net.letsgta.server.api.token.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(

        @NotBlank
        String refreshToken
) {
}
