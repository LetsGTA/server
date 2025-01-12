package org.example.server.api.user.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.server.api.user.application.UserSignUpService;
import org.example.server.api.user.dto.request.UserSignUpRequest;
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
class UserSignUpControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserSignUpService userSignUpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserSignUpController userSignUpController = new UserSignUpController(userSignUpService);
        mockMvc = MockMvcBuilders.standaloneSetup(userSignUpController).build();
    }

    @Test
    @DisplayName("회원가입 - 성공 케이스")
    void signUp_shouldReturnSuccess_whenRequestIsValid() throws Exception {
        // given
        UserSignUpRequest request = new UserSignUpRequest("token123", "test@example.com", "password123", "testuser");

        doNothing().when(userSignUpService).signUp(any(UserSignUpRequest.class));

        // when & then
        mockMvc.perform(post("/api/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"token123\",\"email\":\"test@example.com\",\"password\":\"password123\",\"nickname\":\"testuser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.msg").value("요청 성공"));

        verify(userSignUpService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    @DisplayName("회원가입 - 유효성 검증 실패")
    void signUp_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        // given
        String invalidRequest = "{\"token\":\"\",\"email\":\"invalid-email\",\"password\":\"\",\"nickname\":\"\"}";

        // when & then
        mockMvc.perform(post("/api/v1/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").doesNotExist())
                .andExpect(jsonPath("$.msg").doesNotExist());

        verify(userSignUpService, never()).signUp(any(UserSignUpRequest.class));
    }
}
