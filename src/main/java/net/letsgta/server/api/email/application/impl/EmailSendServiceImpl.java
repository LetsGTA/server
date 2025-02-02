package net.letsgta.server.api.email.application.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.email.application.EmailSendService;
import net.letsgta.server.api.email.dto.request.EmailSendRequest;
import net.letsgta.server.util.email.EmailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendServiceImpl implements EmailSendService {

    private final EmailUtil emailUtil;

    @Value("${aws.ses.from}")
    private String from;

    @Override
    public void sendEmail(String email, String verificationNumber) {
        // send verification email
        emailUtil.send(EmailSendRequest.builder()
                .from(from)
                .to(List.of(email))
                .subject("Let's GTA - 회원가입 인증번호 안내")
                .content("안녕하세요, Let's GTA 입니다.<br><br>" +
                        "회원가입을 위해서 아래 인증코드를 입력해주세요." +
                        "<br>인증번호는 <b>" +
                        verificationNumber +
                        "</b> 입니다.")
                .build());
    }
}
