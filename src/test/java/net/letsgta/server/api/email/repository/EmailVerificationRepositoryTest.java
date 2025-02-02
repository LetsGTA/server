package net.letsgta.server.api.email.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.letsgta.server.api.email.dto.request.EmailVerificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class EmailVerificationRepositoryTest {

    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private static final String TEST_NAME = "test";
    private static final String TEST_TOKEN = "token123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_VERIFICATION_NUMBER = "123456";
    private static final String TEST_KEY = "emailtest:token123";

    private EmailVerificationRequest testRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailVerificationRepository = new EmailVerificationRepository(redisTemplate);
        testRequest = EmailVerificationRequest.builder()
                .email(TEST_EMAIL)
                .verificationToken(TEST_TOKEN)
                .verificationNumber(TEST_VERIFICATION_NUMBER)
                .attemptCount(0)
                .isDone(false)
                .build();
    }

    @Test
    @DisplayName("이메일 저장 - 성공")
    void saveEmail_shouldSaveEmailVerificationRequestSuccessfully() {
        // given
        long timeout = 30000L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        emailVerificationRepository.saveEmail(TEST_NAME, testRequest, timeout);

        // then
        verify(valueOperations, times(1)).set(
                TEST_KEY,
                testRequest,
                timeout,
                TimeUnit.MILLISECONDS
        );
    }

    @Test
    @DisplayName("이메일 조회 - 성공")
    void findByVerificationToken_shouldReturnRequest_whenTokenExists() {
        // given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(TEST_KEY)).thenReturn(testRequest);
        when(redisTemplate.keys(TEST_KEY)).thenReturn(Set.of(TEST_KEY));

        // when
        EmailVerificationRequest result = emailVerificationRepository.findByVerificationToken(TEST_NAME, TEST_TOKEN);

        // then
        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.email());
        assertEquals(TEST_TOKEN, result.verificationToken());
        verify(redisTemplate.opsForValue(), times(1)).get(TEST_KEY);
    }

    @Test
    @DisplayName("이메일 삭제 - 성공")
    void deleteByVerificationToken_shouldDeleteEmailVerificationRequestSuccessfully() {
        // given
        when(redisTemplate.keys(TEST_KEY)).thenReturn(Set.of(TEST_KEY));

        // when
        emailVerificationRepository.deleteByVerificationToken(TEST_NAME, TEST_TOKEN);

        // then
        verify(redisTemplate, times(1)).delete(TEST_KEY);
    }

    @Test
    @DisplayName("이메일 인증번호 검증 - 성공")
    void isVerificationNumberValid_shouldReturnTrue_whenNumbersMatch() {
        // when
        boolean isValid = testRequest.isVerificationNumberValid(TEST_VERIFICATION_NUMBER);

        // then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("이메일 인증번호 검증 - 실패")
    void isVerificationNumberValid_shouldReturnFalse_whenNumbersDoNotMatch() {
        // when
        boolean isValid = testRequest.isVerificationNumberValid("654321");

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("이메일 시도 횟수 증가 - 성공")
    void incrementAttemptCount_shouldIncreaseAttemptCount() {
        // given
        EmailVerificationRequest updatedRequest = testRequest.incrementAttemptCount();

        // then
        assertEquals(1, updatedRequest.attemptCount());
        assertEquals(TEST_EMAIL, updatedRequest.email());
    }

    @Test
    @DisplayName("이메일 인증 완료 상태 변경 - 성공")
    void markAsDone_shouldUpdateIsDoneToTrue() {
        // when
        EmailVerificationRequest updatedRequest = testRequest.markAsDone();

        // then
        assertTrue(updatedRequest.isDone());
        assertEquals(TEST_EMAIL, updatedRequest.email());
    }
}
