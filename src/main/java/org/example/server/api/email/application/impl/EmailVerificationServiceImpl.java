package org.example.server.api.email.application.impl;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.example.server.api.email.application.EmailSendService;
import org.example.server.api.email.application.EmailVerificationService;
import org.example.server.api.email.dto.request.EmailSignUpRequest;
import org.example.server.api.email.dto.request.EmailSignUpVerificationRequest;
import org.example.server.api.email.dto.request.EmailVerificationRequest;
import org.example.server.api.email.dto.response.EmailSignUpResponse;
import org.example.server.api.email.exception.EmailException;
import org.example.server.api.email.exception.EmailExceptionResult;
import org.example.server.api.email.repository.EmailVerificationRepository;
import org.example.server.api.user.application.UserGetService;
import org.example.server.api.user.exception.UserException;
import org.example.server.api.user.exception.UserExceptionResult;
import org.example.server.util.random.RandomUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final String EMAIL_TYPE = "SignUp";
    private static final int MAX_ATTEMPT = 5;

    private final UserGetService userGetService;
    private final EmailSendService emailSendService;
    private final EmailVerificationRepository emailVerificationRepository;

    @Value("${aws.ses.expiration}")
    private long expiration;

    @Transactional
    @Override
    public EmailSignUpResponse sendEmailVerification(EmailSignUpRequest request) {
        // check duplicate
        if(userGetService.isEmailExist(request.email())) {
            throw new UserException(UserExceptionResult.ALREADY_EXISTS);
        }

        String verificationToken = RandomUtil.generateRandomString(32);
        String verificationNumber = RandomUtil.generateRandomNumber(6);

        // send verification email
        emailSendService.sendEmail(request.email(), verificationNumber);

        // save verification email in redis
        EmailVerificationRequest emailVerificationRequest = EmailVerificationRequest.builder()
                .email(request.email())
                .verificationToken(verificationToken)
                .verificationNumber(verificationNumber)
                .attemptCount(0)
                .isDone(false)
                .build();
        emailVerificationRepository.saveEmail(EMAIL_TYPE, emailVerificationRequest, expiration);

        return new EmailSignUpResponse(verificationToken);
    }

    @Transactional
    @Override
    public void verifyEmail(EmailSignUpVerificationRequest request) {
        EmailVerificationRequest emailVerificationRequest = emailVerificationRepository.findByVerificationToken(
                EMAIL_TYPE,
                request.token()
        );

        if (emailVerificationRequest == null) {
            throw new EmailException(EmailExceptionResult.EXPIRED);
        }

        if (emailVerificationRequest.isDone()) {
            throw new EmailException(EmailExceptionResult.ALREADY_VERIFIED);
        }

        if (emailVerificationRequest.isAttemptLimitExceeded(MAX_ATTEMPT)) {
            throw new EmailException(EmailExceptionResult.TOO_MANY_ATTEMPTS);
        }

        if (!emailVerificationRequest.verificationNumber().equals(request.verificationNumber())) {
            EmailVerificationRequest updatedRequest = emailVerificationRequest.incrementAttemptCount();
            emailVerificationRepository.saveEmail(EMAIL_TYPE, updatedRequest, expiration);
            throw new EmailException(EmailExceptionResult.INVALID_VERIFICATION_NUMBER);
        }

        EmailVerificationRequest updatedRequest = emailVerificationRequest.markAsDone();
        emailVerificationRepository.saveEmail(EMAIL_TYPE, updatedRequest, expiration);
    }


    @Override
    public void confirmEmail(String token) {
        EmailVerificationRequest emailVerificationRequest = emailVerificationRepository.findByVerificationToken(
                EMAIL_TYPE,
                token
        );

        if (Objects.isNull(emailVerificationRequest)) {
            throw new EmailException(EmailExceptionResult.EXPIRED);
        }

        if (!emailVerificationRequest.isDone()) {
            throw new EmailException(EmailExceptionResult.VERIFICATION_NOT_COMPLETE);
        }
    }

    @Override
    public void completeEmailVerification(String token) {
        emailVerificationRepository.deleteByVerificationToken(EMAIL_TYPE, token);
    }
}
