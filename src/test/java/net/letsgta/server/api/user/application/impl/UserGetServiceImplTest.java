package net.letsgta.server.api.user.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.letsgta.server.api.user.dto.response.UserGetResponse;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.exception.UserException;
import net.letsgta.server.api.user.exception.UserExceptionResult;
import net.letsgta.server.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class UserGetServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserGetServiceImpl userGetService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = User.builder()
                .userId(1L)
                .email("test@example.com")
                .nickname("testuser")
                .build();

        userRepository.save(testUser);
    }

    @Test
    @DisplayName("사용자 조회 - ID로 조회 성공")
    void getUserByUserId_shouldReturnUserGetResponse_whenUserExists() {
        // given
        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(testUser));

        // when
        UserGetResponse response = userGetService.getUserByUserId(1L);

        // then
        assertNotNull(response);
        assertEquals("test@example.com", response.email());
        verify(userRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("사용자 조회 - ID로 조회 실패")
    void getUserByUserId_shouldThrowException_whenUserDoesNotExist() {
        // given
        when(userRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userGetService.getUserByUserId(1L));
        assertEquals(UserExceptionResult.NOT_EXISTS, exception.getUserExceptionResult());
        verify(userRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("사용자 조회 - 이메일로 조회 성공")
    void getUserByEmail_shouldReturnUserGetResponse_whenEmailExists() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // when
        UserGetResponse response = userGetService.getUserByEmail("test@example.com");

        // then
        assertNotNull(response);
        assertEquals("test@example.com", response.email());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("사용자 조회 - 이메일로 조회 실패")
    void getUserByEmail_shouldThrowException_whenEmailDoesNotExist() {
        // given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userGetService.getUserByEmail("nonexistent@example.com"));
        assertEquals(UserExceptionResult.NOT_EXISTS, exception.getUserExceptionResult());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재할 경우")
    void isEmailExist_shouldReturnTrue_whenUserExists() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // when
        boolean result = userGetService.isUserExist("test@example.com");

        // then
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않을 경우")
    void isEmailExist_shouldReturnFalse_whenUserDoesNotExist() {
        // given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // when
        boolean result = userGetService.isUserExist("nonexistent@example.com");

        // then
        assertFalse(result);
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }
}
