package org.example.server.config.security.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("Access Token 생성 - 성공")
    void generateAccessToken_shouldCreateTokenSuccessfully() {
        // given
        String userId = "123";

        // when
        String token = jwtProvider.generateAccessToken(userId);

        // then
        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(userId, jwtProvider.getUsernameFromJwtToken(token));
    }

    @Test
    @DisplayName("Refresh Token 생성 - 성공")
    void generateRefreshToken_shouldCreateTokenSuccessfully() {
        // given
        String userId = "123";

        // when
        String token = jwtProvider.generateRefreshToken(userId);

        // then
        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(userId, jwtProvider.getUsernameFromJwtToken(token));
    }

    @Test
    @DisplayName("JWT 검증 - 만료된 토큰")
    void validateToken_shouldReturnFalse_whenTokenIsExpired() throws InterruptedException {
        // given
        String userId = "123";
        String token = Jwts.builder()
                .setId(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 100))
                .compact();

        // Wait for token to expire
        Thread.sleep(200);

        // when
        boolean isValid = jwtProvider.validateToken(token);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("JWT 검증 - 잘못된 서명")
    void validateToken_shouldReturnFalse_whenSignatureIsInvalid() {
        // given
        String userId = "123";
        String token = Jwts.builder()
                .setId(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 100))
                .compact();

        // when
        boolean isValid = jwtProvider.validateToken(token);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("JWT 에서 클레임 추출 - 성공")
    void getClaimFromToken_shouldExtractClaimsSuccessfully() {
        // given
        String userId = "123";
        String token = jwtProvider.generateAccessToken(userId);

        // when
        String extractedUserId = jwtProvider.getUsernameFromJwtToken(token);

        // then
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("JWT 검증 - 잘못된 토큰")
    void validateToken_shouldReturnFalse_whenTokenIsMalformed() {
        // given
        String malformedToken = "malformed.token";

        // when
        boolean isValid = jwtProvider.validateToken(malformedToken);

        // then
        assertFalse(isValid);
    }
}
