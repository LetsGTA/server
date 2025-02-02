package net.letsgta.server.api.login.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(

        String accessToken,

        String refreshToken
) {
}
