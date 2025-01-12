package org.example.server.api.login.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.server.api.login.dto.request.LoginRequest;
import org.example.server.api.login.dto.response.LoginResponse;
import org.example.server.api.login.exception.LoginException;
import org.example.server.api.login.exception.LoginExceptionResult;
import org.example.server.api.token.repository.RefreshTokenRepository;
import org.example.server.api.user.application.UserGetService;
import org.example.server.api.user.dto.response.UserGetResponse;
import org.example.server.api.user.exception.UserException;
import org.example.server.api.user.exception.UserExceptionResult;
import org.example.server.config.security.provider.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class LoginServiceImplTest {

    private LoginServiceImpl loginService;

    @Mock
    private UserGetService userGetService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginServiceImpl(userGetService, bCryptPasswordEncoder, jwtProvider, refreshTokenRepository);
    }

    @Test
    @DisplayName("로그인 - 성공 케이스")
    void login_shouldReturnTokens_whenCredentialsAreCorrect() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "correctPassword");
        UserGetResponse userInfo = UserGetResponse.builder()
                .userId(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("testuser")
                .role(null)
                .build();
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        when(userGetService.getUserByEmail("test@example.com")).thenReturn(userInfo);
        when(bCryptPasswordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateAccessToken(1L)).thenReturn(accessToken);
        when(jwtProvider.generateRefreshToken(1L)).thenReturn(refreshToken);

        // when
        LoginResponse response = loginService.login(request);

        // then
        assertNotNull(response);
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshToken, response.refreshToken());

        verify(userGetService, times(1)).getUserByEmail("test@example.com");
        verify(bCryptPasswordEncoder, times(1)).matches("correctPassword", "encodedPassword");
        verify(jwtProvider, times(1)).generateAccessToken(1L);
        verify(jwtProvider, times(1)).generateRefreshToken(1L);
        verify(refreshTokenRepository, times(1)).saveRefreshToken(refreshToken, 1L);
    }

    @Test
    @DisplayName("로그인 - 비밀번호 불일치")
    void login_shouldThrowException_whenPasswordIsIncorrect() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        UserGetResponse userInfo = UserGetResponse.builder()
                .userId(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("testuser")
                .role(null)
                .build();

        when(userGetService.getUserByEmail("test@example.com")).thenReturn(userInfo);
        when(bCryptPasswordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // when & then
        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(request));

        assertEquals(LoginExceptionResult.NOT_CORRECT, exception.getLoginExceptionResult());

        verify(userGetService, times(1)).getUserByEmail("test@example.com");
        verify(bCryptPasswordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
        verify(jwtProvider, never()).generateAccessToken(anyLong());
        verify(jwtProvider, never()).generateRefreshToken(anyLong());
        verify(refreshTokenRepository, never()).saveRefreshToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 사용자")
    void login_shouldThrowException_whenUserDoesNotExist() {
        // given
        LoginRequest request = new LoginRequest("nonexistent@example.com", "anyPassword");

        when(userGetService.getUserByEmail("nonexistent@example.com"))
                .thenThrow(new UserException(UserExceptionResult.NOT_EXISTS));

        // when & then
        UserException exception = assertThrows(UserException.class, () -> loginService.login(request));

        assertEquals(UserExceptionResult.NOT_EXISTS, exception.getUserExceptionResult());

        verify(userGetService, times(1)).getUserByEmail("nonexistent@example.com");
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
        verify(jwtProvider, never()).generateAccessToken(anyLong());
        verify(jwtProvider, never()).generateRefreshToken(anyLong());
        verify(refreshTokenRepository, never()).saveRefreshToken(anyString(), anyLong());
    }
}
