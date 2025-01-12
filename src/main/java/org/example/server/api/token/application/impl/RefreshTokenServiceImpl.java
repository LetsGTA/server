package org.example.server.api.token.application.impl;

import lombok.RequiredArgsConstructor;
import org.example.server.api.token.application.RefreshTokenService;
import org.example.server.api.token.dto.response.RefreshTokenResponse;
import org.example.server.api.token.exception.RefreshTokenException;
import org.example.server.api.token.exception.RefreshTokenExceptionResult;
import org.example.server.api.token.repository.RefreshTokenRepository;
import org.example.server.config.security.provider.JwtProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        // refresh token 유효성 검증
        checkRefreshToken(refreshToken);

        // refresh token userId 조회
        var userId = refreshTokenRepository.getRefreshToken(refreshToken);

        // 새로운 access token 생성
        String newAccessToken = jwtProvider.generateAccessToken(userId);

        // 기존의 refresh token 제거
        refreshTokenRepository.removeRefreshToken(refreshToken);

        // 새로운 refresh token 생성 후 저장
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);
        refreshTokenRepository.saveRefreshToken(newRefreshToken, userId);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private void checkRefreshToken(final String refreshToken) {
        if(Boolean.FALSE.equals(jwtProvider.validateToken(refreshToken)))
            throw new RefreshTokenException(RefreshTokenExceptionResult.INVALID);
    }
}
