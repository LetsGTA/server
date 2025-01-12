package org.example.server.api.email.application;

import org.example.server.api.email.dto.request.EmailSignUpRequest;
import org.example.server.api.email.dto.request.EmailSignUpVerificationRequest;
import org.example.server.api.email.dto.response.EmailSignUpResponse;

public interface EmailVerificationService {

    EmailSignUpResponse sendEmailVerification(EmailSignUpRequest request);
    void verifyEmail(EmailSignUpVerificationRequest request);
    void confirmEmail(String token);
    void completeEmailVerification(String token);
}
