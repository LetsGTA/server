package org.example.server.api.login.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.server.api.login.application.LoginService;
import org.example.server.api.login.dto.request.LoginRequest;
import org.example.server.api.login.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ActiveProfiles("test")
class LoginControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LoginController loginController = new LoginController(loginService);
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    @DisplayName("로그인 - 성공 케이스")
    void login_shouldReturnTokens_whenRequestIsValid() throws Exception {
        // given
        LoginResponse response = LoginResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        when(loginService.login(any(LoginRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.msg").value("요청 성공"))
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"));

        verify(loginService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("로그인 - 유효성 검증 실패")
    void login_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        // given
        String invalidRequest = "{\"email\":\"\",\"password\":\"\"}";

        // when & then
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").doesNotExist())
                .andExpect(jsonPath("$.msg").doesNotExist());

        verify(loginService, never()).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("로그인 - 이메일 형식 오류")
    void login_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        // given
        String invalidRequest = "{\"email\":\"invalid-email\",\"password\":\"password123\"}";

        // when & then
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").doesNotExist())
                .andExpect(jsonPath("$.msg").doesNotExist());

        verify(loginService, never()).login(any(LoginRequest.class));
    }
}
