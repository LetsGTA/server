package org.example.server.api.token.dto.response;

import lombok.Builder;

@Builder
public record RefreshTokenResponse(

        String accessToken,

        String refreshToken
) {
}
