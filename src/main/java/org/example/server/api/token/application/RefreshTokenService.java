package org.example.server.api.token.application;

import org.example.server.api.token.dto.response.RefreshTokenResponse;

public interface RefreshTokenService {

    RefreshTokenResponse refreshToken(final String refreshToken);
}
