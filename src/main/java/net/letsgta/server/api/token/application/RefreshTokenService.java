package net.letsgta.server.api.token.application;

import net.letsgta.server.api.token.dto.response.RefreshTokenResponse;

public interface RefreshTokenService {

    RefreshTokenResponse refreshToken(final String refreshToken);
}
