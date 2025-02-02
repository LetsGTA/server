package net.letsgta.server.api.email.application;

import net.letsgta.server.api.email.dto.request.EmailSignUpRequest;
import net.letsgta.server.api.email.dto.request.EmailSignUpVerificationRequest;
import net.letsgta.server.api.email.dto.response.EmailSignUpResponse;

public interface EmailVerificationService {

    EmailSignUpResponse sendEmailVerification(EmailSignUpRequest request);
    void verifyEmail(EmailSignUpVerificationRequest request);
    void confirmEmail(String token);
    void completeEmailVerification(String token);
}
