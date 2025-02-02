package net.letsgta.server.api.token.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.letsgta.server.api.token.dto.response.RefreshTokenResponse;
import net.letsgta.server.api.token.exception.RefreshTokenException;
import net.letsgta.server.api.token.exception.RefreshTokenExceptionResult;
import net.letsgta.server.api.token.repository.RefreshTokenRepository;
import net.letsgta.server.config.security.provider.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class RefreshTokenServiceImplTest {

    private RefreshTokenServiceImpl refreshTokenService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refreshTokenService = new RefreshTokenServiceImpl(jwtProvider, refreshTokenRepository);
    }

    @Test
    @DisplayName("Refresh Token 갱신 - 성공")
    void refreshToken_shouldReturnNewTokens_whenRefreshTokenIsValid() {
        // given
        String oldRefreshToken = "validRefreshToken";
        Long userId = 123L;
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        when(jwtProvider.validateToken(oldRefreshToken)).thenReturn(true);
        when(refreshTokenRepository.getRefreshToken(oldRefreshToken)).thenReturn(userId);
        when(jwtProvider.generateAccessToken(userId)).thenReturn(newAccessToken);
        when(jwtProvider.generateRefreshToken(userId)).thenReturn(newRefreshToken);

        // when
        RefreshTokenResponse response = refreshTokenService.refreshToken(oldRefreshToken);

        // then
        assertNotNull(response);
        assertEquals(newAccessToken, response.accessToken());
        assertEquals(newRefreshToken, response.refreshToken());

        verify(refreshTokenRepository, times(1)).getRefreshToken(oldRefreshToken);
        verify(refreshTokenRepository, times(1)).removeRefreshToken(oldRefreshToken);
        verify(refreshTokenRepository, times(1)).saveRefreshToken(newRefreshToken, userId);
    }

    @Test
    @DisplayName("Refresh Token 갱신 - 유효하지 않은 토큰")
    void refreshToken_shouldThrowException_whenTokenIsInvalid() {
        // given
        String invalidRefreshToken = "invalidRefreshToken";

        when(jwtProvider.validateToken(invalidRefreshToken)).thenReturn(false);

        // when & then
        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> refreshTokenService.refreshToken(invalidRefreshToken));

        assertEquals(RefreshTokenExceptionResult.INVALID, exception.getRefreshTokenExceptionResult());
        verify(refreshTokenRepository, never()).getRefreshToken(anyString());
        verify(refreshTokenRepository, never()).removeRefreshToken(anyString());
        verify(refreshTokenRepository, never()).saveRefreshToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Refresh Token 갱신 - 존재하지 않는 토큰")
    void refreshToken_shouldThrowException_whenTokenDoesNotExist() {
        // given
        String nonExistentToken = "nonExistentToken";

        when(jwtProvider.validateToken(nonExistentToken)).thenReturn(true);
        when(refreshTokenRepository.getRefreshToken(nonExistentToken))
                .thenThrow(new RefreshTokenException(RefreshTokenExceptionResult.NOT_EXIST));

        // when & then
        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> refreshTokenService.refreshToken(nonExistentToken));

        assertEquals(RefreshTokenExceptionResult.NOT_EXIST, exception.getRefreshTokenExceptionResult());
        verify(refreshTokenRepository, times(1)).getRefreshToken(nonExistentToken);
        verify(refreshTokenRepository, never()).removeRefreshToken(anyString());
        verify(refreshTokenRepository, never()).saveRefreshToken(anyString(), anyLong());
    }
}
