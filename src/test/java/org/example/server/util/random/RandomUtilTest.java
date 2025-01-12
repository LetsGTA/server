package org.example.server.util.random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class RandomUtilTest {

    @Test
    void generateRandomNumber_shouldReturnStringOfGivenLength_withNumericCharacters() {
        // given
        int length = 10;

        // when
        String randomNumber = RandomUtil.generateRandomNumber(length);

        // then
        assertNotNull(randomNumber);
        assertEquals(length, randomNumber.length());
        assertTrue(randomNumber.matches("\\d+"), "Generated string should contain only numeric characters.");
    }

    @Test
    void generateRandomString_shouldReturnStringOfGivenLength_withAlphanumericCharacters() {
        // given
        int length = 15;

        // when
        String randomString = RandomUtil.generateRandomString(length);

        // then
        assertNotNull(randomString);
        assertEquals(length, randomString.length());
        assertTrue(randomString.matches("[a-zA-Z0-9]+"), "Generated string should contain only alphanumeric characters.");
    }

    @Test
    void generateRandomNumber_shouldThrowException_whenLengthIsZeroOrNegative() {
        // given
        int length = 0;
        int length2 = -5;

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> RandomUtil.generateRandomNumber(length));
        assertEquals("길이가 0보다 커야 합니다.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> RandomUtil.generateRandomNumber(length2));
        assertEquals("길이가 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    void generateRandomString_shouldThrowException_whenLengthIsZeroOrNegative() {
        // given
        int length = 0;
        int length2 = -5;

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> RandomUtil.generateRandomString(length));
        assertEquals("길이가 0보다 커야 합니다.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> RandomUtil.generateRandomString(length2));
        assertEquals("길이가 0보다 커야 합니다.", exception.getMessage());
    }
}
