package net.letsgta.server.api.email.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.letsgta.server.api.email.application.EmailSendService;
import net.letsgta.server.api.email.dto.request.EmailSignUpRequest;
import net.letsgta.server.api.email.dto.request.EmailSignUpVerificationRequest;
import net.letsgta.server.api.email.dto.request.EmailVerificationRequest;
import net.letsgta.server.api.email.dto.response.EmailSignUpResponse;
import net.letsgta.server.api.email.exception.EmailException;
import net.letsgta.server.api.email.exception.EmailExceptionResult;
import net.letsgta.server.api.email.repository.EmailVerificationRepository;
import net.letsgta.server.api.user.application.UserGetService;
import net.letsgta.server.api.user.exception.UserException;
import net.letsgta.server.api.user.exception.UserExceptionResult;
import net.letsgta.server.util.random.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class EmailVerificationServiceImplTest {

    private EmailVerificationServiceImpl emailVerificationService;

    @Mock
    private UserGetService userGetService;

    @Mock
    private EmailSendService emailSendService;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "token123";
    private static final String TEST_VERIFICATION_NUMBER = "123456";
    private static final String EXPIRED_TOKEN = "expiredToken";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailVerificationService = new EmailVerificationServiceImpl(userGetService, emailSendService, emailVerificationRepository);
    }

    private EmailVerificationRequest buildVerificationRequest(boolean isDone, int attemptCount) {
        return EmailVerificationRequest.builder()
                .email(TEST_EMAIL)
                .verificationToken(TEST_TOKEN)
                .verificationNumber(TEST_VERIFICATION_NUMBER)
                .attemptCount(attemptCount)
                .isDone(isDone)
                .build();
    }

    @Test
    @DisplayName("이메일 인증 요청 - 성공")
    void sendEmailVerification_shouldSendEmailAndSaveRequest() {
        // given
        EmailSignUpRequest request = new EmailSignUpRequest(TEST_EMAIL);
        String generatedToken = "generatedToken123";
        String generatedVerificationNumber = "654321";

        when(userGetService.isUserExist(TEST_EMAIL)).thenReturn(false);
        Mockito.mockStatic(RandomUtil.class);
        when(RandomUtil.generateRandomString(32)).thenReturn(generatedToken);
        when(RandomUtil.generateRandomNumber(6)).thenReturn(generatedVerificationNumber);

        // when
        EmailSignUpResponse response = emailVerificationService.sendEmailVerification(request);

        // then
        assertNotNull(response);
        assertEquals(generatedToken, response.token());
        verify(emailSendService, times(1)).sendEmail(TEST_EMAIL, generatedVerificationNumber);
        verify(emailVerificationRepository, times(1)).saveEmail(eq("SignUp"), any(EmailVerificationRequest.class), anyLong());
    }

    @Test
    @DisplayName("이메일 인증 요청 - 이미 존재하는 이메일")
    void sendEmailVerification_shouldThrowException_whenEmailAlreadyExists() {
        // given
        EmailSignUpRequest request = new EmailSignUpRequest(TEST_EMAIL);

        when(userGetService.isUserExist(TEST_EMAIL)).thenReturn(true);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> emailVerificationService.sendEmailVerification(request));
        assertEquals(UserExceptionResult.ALREADY_EXISTS, exception.getUserExceptionResult());
        verify(emailSendService, never()).sendEmail(anyString(), anyString());
        verify(emailVerificationRepository, never()).saveEmail(anyString(), any(), anyLong());
    }

    @Test
    @DisplayName("이메일 인증 검증 - 성공")
    void verifyEmail_shouldMarkAsDone_whenVerificationIsValid() {
        // given
        EmailSignUpVerificationRequest request = new EmailSignUpVerificationRequest(TEST_TOKEN, TEST_VERIFICATION_NUMBER);
        EmailVerificationRequest verificationRequest = buildVerificationRequest(false, 0);

        when(emailVerificationRepository.findByVerificationToken("SignUp", TEST_TOKEN)).thenReturn(verificationRequest);

        // when
        emailVerificationService.verifyEmail(request);

        // then
        verify(emailVerificationRepository, times(1)).saveEmail(eq("SignUp"), argThat(EmailVerificationRequest::isDone), anyLong());
    }

    @Test
    @DisplayName("이메일 인증 검증 - 잘못된 인증번호")
    void verifyEmail_shouldThrowException_whenVerificationNumberIsInvalid() {
        // given
        EmailSignUpVerificationRequest request = new EmailSignUpVerificationRequest(TEST_TOKEN, "wrongNumber");
        EmailVerificationRequest verificationRequest = buildVerificationRequest(false, 0);

        when(emailVerificationRepository.findByVerificationToken("SignUp", TEST_TOKEN)).thenReturn(verificationRequest);

        // when & then
        EmailException exception = assertThrows(EmailException.class, () -> emailVerificationService.verifyEmail(request));
        assertEquals(EmailExceptionResult.INVALID_VERIFICATION_NUMBER, exception.getEmailExceptionResult());
        verify(emailVerificationRepository, times(1)).saveEmail(eq("SignUp"), any(EmailVerificationRequest.class), anyLong());
    }

    @Test
    @DisplayName("이메일 인증 검증 - 인증 시도 횟수 초과")
    void verifyEmail_shouldThrowException_whenAttemptLimitExceeded() {
        // given
        EmailSignUpVerificationRequest request = new EmailSignUpVerificationRequest(TEST_TOKEN, TEST_VERIFICATION_NUMBER);
        EmailVerificationRequest verificationRequest = buildVerificationRequest(false, 5);

        when(emailVerificationRepository.findByVerificationToken("SignUp", TEST_TOKEN)).thenReturn(verificationRequest);

        // when & then
        EmailException exception = assertThrows(EmailException.class, () -> emailVerificationService.verifyEmail(request));
        assertEquals(EmailExceptionResult.TOO_MANY_ATTEMPTS, exception.getEmailExceptionResult());
        verify(emailVerificationRepository, never()).saveEmail(eq("SignUp"), any(EmailVerificationRequest.class), anyLong());
    }

    @Test
    @DisplayName("이메일 인증 검증 - 이미 인증 완료")
    void verifyEmail_shouldThrowException_whenAlreadyVerified() {
        // given
        EmailSignUpVerificationRequest request = new EmailSignUpVerificationRequest(TEST_TOKEN, TEST_VERIFICATION_NUMBER);
        EmailVerificationRequest verificationRequest = buildVerificationRequest(true, 0);

        when(emailVerificationRepository.findByVerificationToken("SignUp", TEST_TOKEN)).thenReturn(verificationRequest);

        // when & then
        EmailException exception = assertThrows(EmailException.class, () -> emailVerificationService.verifyEmail(request));
        assertEquals(EmailExceptionResult.ALREADY_VERIFIED, exception.getEmailExceptionResult());
        verify(emailVerificationRepository, never()).saveEmail(eq("SignUp"), any(EmailVerificationRequest.class), anyLong());
    }

    @Test
    @DisplayName("이메일 인증 확인 - 성공")
    void confirmEmail_shouldDoNothing_whenVerificationIsDone() {
        // given
        EmailVerificationRequest verificationRequest = buildVerificationRequest(true, 0);

        when(emailVerificationRepository.findByVerificationToken("SignUp", TEST_TOKEN)).thenReturn(verificationRequest);

        // when
        emailVerificationService.confirmEmail(TEST_TOKEN);

        // then
        verify(emailVerificationRepository, times(1)).findByVerificationToken("SignUp", TEST_TOKEN);
    }

    @Test
    @DisplayName("이메일 인증 확인 - 실패")
    void confirmEmail_shouldThrowException_whenVerificationIsNotDone() {
        // given
        EmailVerificationRequest verificationRequest = buildVerificationRequest(false, 0);

        when(emailVerificationRepository.findByVerificationToken("SignUp", TEST_TOKEN)).thenReturn(verificationRequest);

        // when & then
        EmailException exception = assertThrows(EmailException.class, () -> emailVerificationService.confirmEmail(TEST_TOKEN));
        assertEquals(EmailExceptionResult.VERIFICATION_NOT_COMPLETE, exception.getEmailExceptionResult());
        verify(emailVerificationRepository, times(1)).findByVerificationToken("SignUp", TEST_TOKEN);
    }

    @Test
    @DisplayName("이메일 인증 확인 - 토큰 만료")
    void confirmEmail_shouldThrowException_whenTokenIsExpired() {
        // given
        when(emailVerificationRepository.findByVerificationToken("SignUp", EXPIRED_TOKEN)).thenReturn(null);

        // when & then
        EmailException exception = assertThrows(EmailException.class, () -> emailVerificationService.confirmEmail(EXPIRED_TOKEN));
        assertEquals(EmailExceptionResult.EXPIRED, exception.getEmailExceptionResult());
        verify(emailVerificationRepository, times(1)).findByVerificationToken("SignUp", EXPIRED_TOKEN);
    }
}
