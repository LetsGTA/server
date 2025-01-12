package org.example.server.api.token.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.server.api.token.application.RefreshTokenService;
import org.example.server.api.token.dto.response.RefreshTokenResponse;
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
class RefreshTokenControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        RefreshTokenController refreshTokenController = new RefreshTokenController(refreshTokenService);
        mockMvc = MockMvcBuilders.standaloneSetup(refreshTokenController).build();
    }

    @Test
    @DisplayName("Token Refresh - 성공")
    void tokenRefresh_shouldReturnNewTokens_whenRequestIsValid() throws Exception {
        // given
        RefreshTokenResponse response = RefreshTokenResponse.builder()
                .accessToken("newAccessToken")
                .refreshToken("newRefreshToken")
                .build();

        when(refreshTokenService.refreshToken(any())).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/login/token-refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"validRefreshToken\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.msg").value("요청 성공"))
                .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("newRefreshToken"));

        verify(refreshTokenService, times(1)).refreshToken("validRefreshToken");
    }

    @Test
    @DisplayName("Token Refresh - 유효성 검증 실패")
    void tokenRefresh_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        // given
        String invalidRequest = "{\"refreshToken\":\"\"}";

        // when & then
        mockMvc.perform(post("/api/v1/login/token-refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").doesNotExist())
                .andExpect(jsonPath("$.msg").doesNotExist());

        verify(refreshTokenService, never()).refreshToken(any());
    }
}
