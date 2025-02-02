package net.letsgta.server.util.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import net.letsgta.server.api.email.dto.request.EmailSendRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class EmailUtilTest {

    @Mock
    private AmazonSimpleEmailService emailService;

    @InjectMocks
    private EmailUtil emailUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void send_success() {
        // given
        EmailSendRequest request = mock(EmailSendRequest.class);
        when(request.toSendEmailRequest()).thenReturn(new com.amazonaws.services.simpleemail.model.SendEmailRequest());

        // when & then
        assertDoesNotThrow(() -> emailUtil.send(request));
        verify(emailService, times(1)).sendEmail(any(com.amazonaws.services.simpleemail.model.SendEmailRequest.class));
    }

    @Test
    void send_failure() {
        // given
        EmailSendRequest request = mock(EmailSendRequest.class);
        when(request.toSendEmailRequest()).thenReturn(new com.amazonaws.services.simpleemail.model.SendEmailRequest());
        doThrow(new RuntimeException("AWS SES error"))
                .when(emailService).sendEmail(any(com.amazonaws.services.simpleemail.model.SendEmailRequest.class));

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> emailUtil.send(request));

        // then
        assertEquals("이메일 전송 서비스가 원활하지 않습니다.", exception.getMessage());
        verify(emailService, times(1)).sendEmail(any(com.amazonaws.services.simpleemail.model.SendEmailRequest.class));
    }
}

