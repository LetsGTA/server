package org.example.server.util.random;

import java.security.SecureRandom;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String NUMERIC_CHARACTERS = "0123456789";
    private static final String ALPHANUMERIC_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String generateRandomNumber(int length) {
        return generateRandomString(NUMERIC_CHARACTERS, length);
    }

    public static String generateRandomString(int length) {
        return generateRandomString(ALPHANUMERIC_CHARACTERS, length);
    }

    private static String generateRandomString(String characterSet, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("길이가 0보다 커야 합니다.");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(characterSet.length());
            sb.append(characterSet.charAt(index));
        }
        return sb.toString();
    }
}
