package net.letsgta.server.api.user.application.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.letsgta.server.api.email.application.EmailVerificationService;
import net.letsgta.server.api.user.dto.request.UserSignUpRequest;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserSignUpServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserSignUpServiceImpl userSignUpService;

    private UserSignUpRequest userSignUpRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
                .userId(1L)
                .email("test@example.com")
                .nickname("testuser")
                .password("password123")
                .build();

        userSignUpRequest = new UserSignUpRequest("token123", "test@example.com", "password123", "testuser");
    }

    @Test
    @DisplayName("회원가입 - 성공 케이스")
    void signUp_shouldSaveUser_whenRequestIsValid() {
        // given
        doNothing().when(emailVerificationService).confirmEmail(anyString());
        when(bCryptPasswordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        userSignUpService.signUp(userSignUpRequest);

        // then
        verify(emailVerificationService, times(1)).confirmEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailVerificationService, times(1)).completeEmailVerification(anyString());
    }
}
