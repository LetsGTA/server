package net.letsgta.server.api.user.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
class UserDelServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDelServiceImpl userDelService;

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
    @DisplayName("사용자 삭제 - 성공 케이스")
    void deleteUserByUserId_shouldDeleteUser_whenUserExists() {
        // given
        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(testUser));

        // when
        userDelService.deleteUserByUserId(1L);

        // then
        verify(userRepository, times(1)).findByUserId(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    @DisplayName("사용자 삭제 - 사용자 존재하지 않을 때 예외 발생")
    void deleteUserByUserId_shouldThrowException_whenUserDoesNotExist() {
        // given
        when(userRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userDelService.deleteUserByUserId(1L));

        // then
        assertEquals(UserExceptionResult.NOT_EXISTS, exception.getUserExceptionResult());
        verify(userRepository, times(1)).findByUserId(1L);
        verify(userRepository, never()).delete(any());
    }
}
