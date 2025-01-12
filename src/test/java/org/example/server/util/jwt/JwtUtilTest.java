package org.example.server.util.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class JwtUtilTest {

    @Test
    void getLoginId_shouldReturnLoginId_whenAuthenticationIsValid() {
        // given
        Authentication authentication = mock(Authentication.class);
        given(authentication.isAuthenticated()).willReturn(true);
        given(authentication.getPrincipal()).willReturn("12345");

        // when
        long loginId = JwtUtil.getLoginId(authentication);

        // then
        assertEquals(12345L, loginId);
    }

    @Test
    void getLoginId_shouldThrowAccessDeniedException_whenAuthenticationIsNull() {
        // given

        // when & then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> JwtUtil.getLoginId(null));
        assertEquals("로그인 정보가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void getLoginId_shouldThrowAccessDeniedException_whenAuthenticationIsNotAuthenticated() {
        // given
        Authentication authentication = mock(Authentication.class);
        given(authentication.isAuthenticated()).willReturn(false);

        // when & then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> JwtUtil.getLoginId(authentication));
        assertEquals("로그인 정보가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    void getLoginId_shouldThrowNumberFormatException_whenPrincipalIsNotANumber() {
        // given
        Authentication authentication = mock(Authentication.class);
        given(authentication.isAuthenticated()).willReturn(true);
        given(authentication.getPrincipal()).willReturn("not_a_number");

        // when & then
        assertThrows(NumberFormatException.class, () -> JwtUtil.getLoginId(authentication));
    }
}
