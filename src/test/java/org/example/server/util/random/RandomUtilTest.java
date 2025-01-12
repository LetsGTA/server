package org.example.server.util.random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class RandomUtilTest {

    @Test
    @DisplayName("랜덤 숫자 생성 - 정상 동작")
    void generateRandomNumber_shouldGenerateNumberSuccessfully() {
        // given
        int length = 10;

        // when
        String randomNumber = RandomUtil.generateRandomNumber(length);

        // then
        assertNotNull(randomNumber);
        assertEquals(length, randomNumber.length());
        assertTrue(randomNumber.matches("\\d+")); // 숫자로만 이루어졌는지 확인
    }

    @Test
    @DisplayName("랜덤 문자열 생성 - 정상 동작")
    void generateRandomString_shouldGenerateStringSuccessfully() {
        // given
        int length = 15;

        // when
        String randomString = RandomUtil.generateRandomString(length);

        // then
        assertNotNull(randomString);
        assertEquals(length, randomString.length());
        assertTrue(randomString.matches("[A-Za-z0-9]+")); // 영숫자로만 이루어졌는지 확인
    }

    @Test
    @DisplayName("랜덤 숫자 생성 - 길이가 0 이하일 때 예외 발생")
    void generateRandomNumber_shouldThrowException_whenLengthIsZeroOrNegative() {
        // given
        int length = 0;

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                RandomUtil.generateRandomNumber(length));
        assertEquals("길이가 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("랜덤 문자열 생성 - 길이가 0 이하일 때 예외 발생")
    void generateRandomString_shouldThrowException_whenLengthIsZeroOrNegative() {
        // given
        int length = -1;

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                RandomUtil.generateRandomString(length));
        assertEquals("길이가 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("랜덤 문자열 생성 - 다양한 길이 테스트")
    void generateRandomString_shouldHandleVariousLengths() {
        // given & when & then
        for (int length = 1; length <= 50; length++) {
            String randomString = RandomUtil.generateRandomString(length);
            assertNotNull(randomString);
            assertEquals(length, randomString.length());
        }
    }
}
