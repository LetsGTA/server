package org.example.server.api.login.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(

        String accessToken,

        String refreshToken
) {
}
