package net.letsgta.server.config.security.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import net.letsgta.server.api.login.exception.LoginException;
import net.letsgta.server.api.login.exception.LoginExceptionResult;
import net.letsgta.server.api.user.application.UserGetService;
import net.letsgta.server.api.user.dto.response.UserGetResponse;
import net.letsgta.server.api.user.enums.RoleName;
import net.letsgta.server.api.user.exception.UserException;
import net.letsgta.server.api.user.exception.UserExceptionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CustomAuthenticationProviderTest {

    private CustomAuthenticationProvider authenticationProvider;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserGetService userGetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationProvider = new CustomAuthenticationProvider(bCryptPasswordEncoder, userGetService);
    }

    @Test
    @DisplayName("인증 - 성공")
    void authenticate_shouldReturnAuthentication_whenCredentialsAreValid() {
        // given
        String email = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        UserGetResponse userInfo = UserGetResponse.builder()
                .email(email)
                .password(encodedPassword)
                .role(RoleName.ROLE_USER)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, rawPassword);

        when(userGetService.getUserByEmail(email)).thenReturn(userInfo);
        when(bCryptPasswordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // when
        Authentication result = authenticationProvider.authenticate(authentication);

        // then
        assertNotNull(result);
        assertEquals(email, result.getPrincipal());
        assertEquals(encodedPassword, result.getCredentials());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("인증 - 비밀번호 불일치")
    void authenticate_shouldThrowException_whenPasswordDoesNotMatch() {
        // given
        String email = "test@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword123";
        UserGetResponse userInfo = UserGetResponse.builder()
                .email(email)
                .password(encodedPassword)
                .role(RoleName.ROLE_USER)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, rawPassword);

        when(userGetService.getUserByEmail(email)).thenReturn(userInfo);
        when(bCryptPasswordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // when & then
        LoginException exception = assertThrows(LoginException.class, () ->
                authenticationProvider.authenticate(authentication));

        assertEquals(LoginExceptionResult.NOT_CORRECT, exception.getLoginExceptionResult());
    }

    @Test
    @DisplayName("인증 - 사용자 정보 없음")
    void authenticate_shouldThrowException_whenUserNotFound() {
        // given
        String email = "notfound@example.com";
        String password = "password123";
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);

        when(userGetService.getUserByEmail(email)).thenThrow(new UserException(UserExceptionResult.NOT_EXISTS));

        // when & then
        UserException exception = assertThrows(UserException.class, () ->
                authenticationProvider.authenticate(authentication));

        assertEquals(UserExceptionResult.NOT_EXISTS, exception.getUserExceptionResult());
    }

    @Test
    @DisplayName("지원 여부 확인 - 지원되는 인증 유형")
    void supports_shouldReturnTrue_whenAuthenticationTypeIsSupported() {
        // when
        boolean result = authenticationProvider.supports(UsernamePasswordAuthenticationToken.class);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("지원 여부 확인 - 지원되지 않는 인증 유형")
    void supports_shouldReturnFalse_whenAuthenticationTypeIsNotSupported() {
        // when
        boolean result = authenticationProvider.supports(String.class);

        // then
        assertFalse(result);
    }
}
