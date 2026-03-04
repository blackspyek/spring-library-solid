package org.pollub.common.adapter;

import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.stream.Collectors;

//start L2 Adapter implementation
/**
 * Secure password generator implementation of IPasswordGenerator.
 * Generates random passwords with mixed character types.
 */
@Component
public class SecurePasswordGeneratorAdapter implements IPasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    private static final Random random = new Random();
    private static final int PASSWORD_LENGTH = 12;

    @Override
    public String generate() {
        StringBuilder password = new StringBuilder();

        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        return password.toString().chars()
                .boxed()
                .map(c -> (char) c.intValue())
                .collect(StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append)
                .toString();
    }
}
//end L2 Adapter implementation