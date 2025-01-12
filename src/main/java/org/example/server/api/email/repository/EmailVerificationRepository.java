package org.example.server.api.email.repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.server.api.email.dto.request.EmailVerificationRequest;
import org.example.server.api.email.exception.EmailException;
import org.example.server.api.email.exception.EmailExceptionResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String EMAIL_PREFIX = "email";

    public void saveEmail(String name, EmailVerificationRequest request, long timeout) {
        String key = EMAIL_PREFIX + name + ":" + request.verificationToken();

        redisTemplate.opsForValue().set(
                key,
                request,
                timeout,
                TimeUnit.MILLISECONDS
        );
    }

    public EmailVerificationRequest findByVerificationToken(String name, String verificationToken) {
        String key = findKey(name, verificationToken);

        return (EmailVerificationRequest) redisTemplate.opsForValue().get(key);
    }

    public void deleteByVerificationToken(String name, String verificationToken) {
        String key = findKey(name, verificationToken);

        redisTemplate.delete(key);
    }

    private String findKey(String name, String verificationToken) {
        String keyPattern = EMAIL_PREFIX + name + ":" + verificationToken;

        return Objects.requireNonNull(redisTemplate.keys(keyPattern)).stream()
                .findFirst()
                .orElseThrow(() -> new EmailException(EmailExceptionResult.NOT_EXIST));
    }
}
