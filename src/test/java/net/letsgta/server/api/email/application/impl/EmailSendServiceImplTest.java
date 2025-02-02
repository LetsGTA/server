package net.letsgta.server.api.email.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import net.letsgta.server.api.email.dto.request.EmailSendRequest;
import net.letsgta.server.util.email.EmailUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("test")
class EmailSendServiceImplTest {

    private EmailSendServiceImpl emailSendService;

    @Mock
    private EmailUtil emailUtil;

    private static final String FROM_EMAIL = "noreply@example.com";
    private static final String TO_EMAIL = "test@example.com";
    private static final String SUBJECT = "Let's GTA - 회원가입 인증번호 안내";
    private static final String CONTENT_TEMPLATE = "안녕하세요, Let's GTA 입니다.<br><br>" +
            "회원가입을 위해서 아래 인증코드를 입력해주세요." +
            "<br>인증번호는 <b>%s</b> 입니다.";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailSendService = new EmailSendServiceImpl(emailUtil);
        ReflectionTestUtils.setField(emailSendService, "from", FROM_EMAIL);
    }

    @Test
    @DisplayName("이메일 발송 - 성공")
    void sendEmail_shouldSendEmailSuccessfully() {
        // given
        String verificationNumber = "123456";
        String expectedContent = String.format(CONTENT_TEMPLATE, verificationNumber);

        // when
        emailSendService.sendEmail(TO_EMAIL, verificationNumber);

        // then
        ArgumentCaptor<EmailSendRequest> captor = ArgumentCaptor.forClass(EmailSendRequest.class);
        verify(emailUtil, times(1)).send(captor.capture());

        EmailSendRequest capturedRequest = captor.getValue();
        assertEquals(FROM_EMAIL, capturedRequest.from());
        assertEquals(List.of(TO_EMAIL), capturedRequest.to());
        assertEquals(SUBJECT, capturedRequest.subject());
        assertEquals(expectedContent, capturedRequest.content());
    }

    @Test
    @DisplayName("이메일 발송 - 실패")
    void sendEmail_shouldThrowException_whenEmailSendingFails() {
        // given
        String verificationNumber = "123456";

        doThrow(new IllegalArgumentException("이메일 전송 서비스가 원활하지 않습니다."))
                .when(emailUtil).send(any(EmailSendRequest.class));

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> emailSendService.sendEmail(TO_EMAIL, verificationNumber));

        assertEquals("이메일 전송 서비스가 원활하지 않습니다.", exception.getMessage());
        verify(emailUtil, times(1)).send(any(EmailSendRequest.class));
    }
}
