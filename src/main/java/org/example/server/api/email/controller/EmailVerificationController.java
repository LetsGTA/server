package org.example.server.api.email.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.api.common.response.entity.ApiResponseEntity;
import org.example.server.api.email.application.EmailVerificationService;
import org.example.server.api.email.dto.request.EmailSignUpRequest;
import org.example.server.api.email.dto.request.EmailSignUpVerificationRequest;
import org.example.server.api.email.dto.response.EmailSignUpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/signup/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponseEntity<EmailSignUpResponse>> sendVerifyEmail(@Valid @RequestBody EmailSignUpRequest request) {
        return ApiResponseEntity.successResponseEntity(emailVerificationService.sendEmailVerification(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponseEntity<Object>> checkVerifyNumber(@Valid @RequestBody EmailSignUpVerificationRequest request) {
        emailVerificationService.verifyEmail(request);
        return ApiResponseEntity.successResponseEntity();
    }
}
