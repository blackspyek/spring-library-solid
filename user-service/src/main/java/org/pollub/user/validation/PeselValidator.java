package org.pollub.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for PESEL (Polish ID number).
 * PESEL format: 11 digits with checksum validation.
 * Weights for checksum: 1,3,7,9,1,3,7,9,1,3
 */
public class PeselValidator implements ConstraintValidator<ValidPesel, String> {
    
    private static final int[] WEIGHTS = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
    private static final int PESEL_LENGTH = 11;

    @Override
    public void initialize(ValidPesel constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Allow null values - use @NotNull if null is not allowed
        if (value == null) {
            return true;
        }

        // Check if PESEL contains only digits
        if (!value.matches("\\d{" + PESEL_LENGTH + "}")) {
            return false;
        }

        // Validate checksum
        return validateChecksum(value);
    }

    /**
     * Validates PESEL checksum.
     * Algorithm: sum of (digit * weight) mod 10, then subtract from 10.
     * The result should equal the last digit.
     */
    private boolean validateChecksum(String pesel) {
        int sum = 0;
        
        // Calculate weighted sum for first 10 digits
        for (int i = 0; i < PESEL_LENGTH - 1; i++) {
            int digit = Character.getNumericValue(pesel.charAt(i));
            sum += (digit * WEIGHTS[i]) % 10;
        }
        
        // Calculate checksum: (10 - (sum mod 10)) mod 10
        int checksum = (10 - (sum % 10)) % 10;
        int lastDigit = Character.getNumericValue(pesel.charAt(PESEL_LENGTH - 1));
        
        return checksum == lastDigit;
    }
}
