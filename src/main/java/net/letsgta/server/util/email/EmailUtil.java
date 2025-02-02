package net.letsgta.server.util.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.letsgta.server.api.email.dto.request.EmailSendRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailUtil {

    private final AmazonSimpleEmailService emailService;

    public void send(EmailSendRequest request) {
        try {
            emailService.sendEmail(request.toSendEmailRequest());
        } catch (Exception e) {
            throw new IllegalArgumentException("이메일 전송 서비스가 원활하지 않습니다.");
        }
    }
}
