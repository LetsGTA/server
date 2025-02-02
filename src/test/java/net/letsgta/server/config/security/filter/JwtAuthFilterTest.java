package net.letsgta.server.config.security.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import net.letsgta.server.api.user.application.UserGetService;
import net.letsgta.server.api.user.dto.response.UserGetResponse;
import net.letsgta.server.api.user.enums.RoleName;
import net.letsgta.server.config.security.provider.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class JwtAuthFilterTest {

    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserGetService userGetService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthFilter = new JwtAuthFilter(jwtProvider, userGetService);
    }

    @Test
    @DisplayName("JWT 인증 필터 - 유효한 토큰")
    void doFilterInternal_shouldAuthenticate_whenTokenIsValid() throws IOException, ServletException {
        // given
        String token = "Bearer validToken";
        String userId = "1";
        UserGetResponse userInfo = UserGetResponse.builder()
                .userId(Long.parseLong(userId))
                .password("encryptedPassword")
                .role(RoleName.ROLE_USER)
                .build();

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtProvider.getUsernameFromJwtToken("validToken")).thenReturn(userId);
        when(userGetService.getUserByUserId(Long.parseLong(userId))).thenReturn(userInfo);

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(userId, String.valueOf(authentication.getPrincipal()));
        assertEquals("encryptedPassword", authentication.getCredentials());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("JWT 인증 필터 - 토큰 없음")
    void doFilterInternal_shouldNotAuthenticate_whenNoTokenProvided() throws IOException, ServletException {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("JWT 인증 필터 - 유효하지 않은 토큰")
    void doFilterInternal_shouldNotAuthenticate_whenTokenIsInvalid() throws IOException, ServletException {
        // given
        String token = "Bearer invalidToken";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtProvider.getUsernameFromJwtToken("invalidToken")).thenReturn(null);

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
