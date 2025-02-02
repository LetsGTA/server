package net.letsgta.server.api.token.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;
import net.letsgta.server.api.token.exception.RefreshTokenException;
import net.letsgta.server.api.token.exception.RefreshTokenExceptionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refreshTokenRepository = new RefreshTokenRepository(redisTemplate);
    }

    @Test
    @DisplayName("Refresh Token 조회 - 성공")
    void getRefreshToken_shouldReturnUserId_whenTokenExists() {
        // given
        String refreshToken = "sampleToken";
        String userId = "123";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refreshToken:" + refreshToken)).thenReturn(userId);

        // when
        Long result = refreshTokenRepository.getRefreshToken(refreshToken);

        // then
        assertNotNull(result);
        assertEquals(123L, result);
        verify(redisTemplate.opsForValue(), times(1)).get("refreshToken:" + refreshToken);
    }

    @Test
    @DisplayName("Refresh Token 조회 - 실패 (존재하지 않음)")
    void getRefreshToken_shouldThrowException_whenTokenDoesNotExist() {
        // given
        String refreshToken = "invalidToken";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refreshToken:" + refreshToken)).thenReturn(null);

        // when & then
        RefreshTokenException exception = assertThrows(RefreshTokenException.class,
                () -> refreshTokenRepository.getRefreshToken(refreshToken));

        assertEquals(RefreshTokenExceptionResult.NOT_EXIST, exception.getRefreshTokenExceptionResult());
        verify(redisTemplate.opsForValue(), times(1)).get("refreshToken:" + refreshToken);
    }

    @Test
    @DisplayName("Refresh Token 저장 - 성공")
    void saveRefreshToken_shouldStoreTokenSuccessfully() {
        // given
        String refreshToken = "newToken";
        Long userId = 456L;
        long refreshTokenValidity = 3600000L; // 1 hour
        ReflectionTestUtils.setField(refreshTokenRepository, "refreshTokenValidity", refreshTokenValidity);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        refreshTokenRepository.saveRefreshToken(refreshToken, userId);

        // then
        verify(valueOperations, times(1)).set(
                "refreshToken:" + refreshToken,
                String.valueOf(userId),
                refreshTokenValidity,
                TimeUnit.MILLISECONDS
        );
    }

    @Test
    @DisplayName("Refresh Token 삭제 - 성공")
    void removeRefreshToken_shouldRemoveTokenSuccessfully() {
        // given
        String refreshToken = "deleteToken";

        // when
        refreshTokenRepository.removeRefreshToken(refreshToken);

        // then
        verify(redisTemplate, times(1)).delete("refreshToken:" + refreshToken);
    }
}
