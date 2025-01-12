package org.example.server.api.email.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.server.api.email.application.EmailVerificationService;
import org.example.server.api.email.dto.request.EmailSignUpRequest;
import org.example.server.api.email.dto.request.EmailSignUpVerificationRequest;
import org.example.server.api.email.dto.response.EmailSignUpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EmailVerificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmailVerificationService emailVerificationService;

    private static final String SEND_ENDPOINT = "/api/v1/signup/email/send";
    private static final String VERIFY_ENDPOINT = "/api/v1/signup/email/verify";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        EmailVerificationController controller = new EmailVerificationController(emailVerificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("이메일 인증 요청 - 성공")
    void sendVerifyEmail_shouldReturnSuccessResponse() throws Exception {
        // given
        EmailSignUpResponse response = new EmailSignUpResponse("verificationToken123");
        when(emailVerificationService.sendEmailVerification(any(EmailSignUpRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post(SEND_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.msg").value("요청 성공"))
                .andExpect(jsonPath("$.data.token").value("verificationToken123"));

        verify(emailVerificationService, times(1)).sendEmailVerification(any(EmailSignUpRequest.class));
    }

    @Test
    @DisplayName("이메일 인증 요청 - 유효성 검증 실패")
    void sendVerifyEmail_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        // when & then
        mockMvc.perform(post(SEND_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(emailVerificationService, never()).sendEmailVerification(any(EmailSignUpRequest.class));
    }

    @Test
    @DisplayName("이메일 인증번호 검증 - 성공")
    void checkVerifyNumber_shouldReturnSuccessResponse() throws Exception {
        // when & then
        mockMvc.perform(post(VERIFY_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"verificationToken123\",\"verificationNumber\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.msg").value("요청 성공"));

        verify(emailVerificationService, times(1)).verifyEmail(any(EmailSignUpVerificationRequest.class));
    }

    @Test
    @DisplayName("이메일 인증번호 검증 - 유효성 검증 실패")
    void checkVerifyNumber_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        // when & then
        mockMvc.perform(post(VERIFY_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"\",\"verificationNumber\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(emailVerificationService, never()).verifyEmail(any(EmailSignUpVerificationRequest.class));
    }
}
