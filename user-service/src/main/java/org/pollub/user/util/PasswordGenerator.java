package org.pollub.user.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for generating random passwords.
 */
public class PasswordGenerator {
    
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;
    
    private static final SecureRandom random = new SecureRandom();
    private static final int PASSWORD_LENGTH = 12;
    
    private PasswordGenerator() {
        // Utility class
    }
    
    /**
     * Generates a random password with at least 12 characters
     * containing uppercase, lowercase, digits, and special characters.
     */
    public static String generatePassword() {
        List<Character> passwordChars = new ArrayList<>();
        
        // Ensure at least one of each type
        passwordChars.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        passwordChars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        passwordChars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        passwordChars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        
        // Fill the rest randomly
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            passwordChars.add(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        
        // Shuffle the password
        Collections.shuffle(passwordChars, random);
        
        StringBuilder password = new StringBuilder();
        for (Character c : passwordChars) {
            password.append(c);
        }
        
        return password.toString();
    }
}
