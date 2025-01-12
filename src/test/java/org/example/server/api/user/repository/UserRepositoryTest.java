package org.example.server.api.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.example.server.api.user.entity.User;
import org.example.server.api.user.enums.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .nickname("testuser")
                .password("password123")
                .role(RoleName.ROLE_USER)
                .build();
    }

    @Test
    @DisplayName("이메일로 사용자 검색 - 이메일이 존재하는 경우")
    void findByEmail_shouldReturnUser_whenEmailExists() {
        // given
        userRepository.save(testUser);

        // when
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // then
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("이메일로 사용자 검색 - 이메일이 존재하지 않는 경우")
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        // when
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // then
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("사용자 ID로 사용자 검색 - ID가 존재하는 경우")
    void findByUserId_shouldReturnUser_whenUserIdExists() {
        // given
        User savedUser = userRepository.save(testUser);

        // when
        Optional<User> foundUser = userRepository.findByUserId(savedUser.getUserId());

        // then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getUserId(), foundUser.get().getUserId());
    }

    @Test
    @DisplayName("사용자 ID로 사용자 검색 - ID가 존재하지 않는 경우")
    void findByUserId_shouldReturnEmpty_whenUserIdDoesNotExist() {
        // when
        Optional<User> foundUser = userRepository.findByUserId(999L);

        // then
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("사용자 저장 - 생성 시간과 업데이트 시간이 자동 설정되는지 확인")
    void save_shouldAutomaticallySetCreatedAtAndUpdatedAt() {
        // when
        User savedUser = userRepository.save(testUser);

        // then
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
        assertEquals(savedUser.getCreatedAt(), savedUser.getUpdatedAt());
    }

    @Test
    @DisplayName("사용자 수정 - 업데이트 시간이 갱신되는지 확인")
    void save_shouldUpdateUpdatedAtOnModification() {
        // given
        User savedUser = userRepository.save(testUser);

        // when
        savedUser = userRepository.findByUserId(savedUser.getUserId()).orElseThrow();
        savedUser = userRepository.save(savedUser);

        // then
        assertNotNull(savedUser.getUpdatedAt());
    }
}
