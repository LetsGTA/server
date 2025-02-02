package net.letsgta.server.api.token.repository;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.token.exception.RefreshTokenException;
import net.letsgta.server.api.token.exception.RefreshTokenExceptionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidity;

    /**
     * Refresh Token 조회
     */
    // TODO : method 이름과 로직 리팩토링
    public Long getRefreshToken(final String refreshToken) {
        // Redis 에서 값 조회
        String userIdStr = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);

        if (userIdStr == null) {
            throw new RefreshTokenException(RefreshTokenExceptionResult.NOT_EXIST);
        }
        
        return Long.parseLong(userIdStr);
    }

    /**
     * Refresh Token 저장
     * @param refreshToken Refresh Token
     * @param userId 사용자 ID
     */
    public void saveRefreshToken(final String refreshToken, Long userId) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + refreshToken,
                String.valueOf(userId),
                refreshTokenValidity,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 특정 Refresh Token 삭제
     */
    public void removeRefreshToken(final String refreshToken) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
    }
}
