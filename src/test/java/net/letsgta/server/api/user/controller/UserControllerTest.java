package net.letsgta.server.api.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import net.letsgta.server.api.user.application.UserDelService;
import net.letsgta.server.api.user.application.UserGetService;
import net.letsgta.server.api.user.dto.response.UserGetResponse;
import net.letsgta.server.util.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ActiveProfiles("test")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserGetService userGetService;

    @Mock
    private UserDelService userDelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserController userController = new UserController(userGetService, userDelService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("대시보드 조회 - 성공")
    void dashboard_shouldReturnUserDetails_whenAuthenticationIsValid() throws Exception {
        // given
        UserGetResponse mockResponse = UserGetResponse.builder()
                .userId(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("testuser")
                .role(null)
                .build();

        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.getLoginId(any())).thenReturn(1L);
            when(userGetService.getUserByUserId(1L)).thenReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/api/v1/user")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value("success"))
                    .andExpect(jsonPath("$.msg").value("요청 성공"))
                    .andExpect(jsonPath("$.data.userId").value(1L))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.nickname").value("testuser"));

            verify(userGetService, times(1)).getUserByUserId(1L);
        }
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void delete_shouldDeleteUser_whenAuthenticationIsValid() throws Exception {
        // given
        try (MockedStatic<JwtUtil> jwtUtilMock = mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.getLoginId(any())).thenReturn(1L);
            doNothing().when(userDelService).deleteUserByUserId(1L);

            // when & then
            mockMvc.perform(delete("/api/v1/user")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value("success"))
                    .andExpect(jsonPath("$.msg").value("요청 성공"));

            verify(userDelService, times(1)).deleteUserByUserId(1L);
        }
    }
}
