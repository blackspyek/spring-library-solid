package org.pollub.user.service.utils;

import lombok.RequiredArgsConstructor;
import org.pollub.common.exception.DisabledUserException;
import org.pollub.common.exception.UserAlreadyExistsException;
import org.pollub.user.model.User;
import org.pollub.user.repository.IUserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final IUserRepository userRepository;

    public void validateNewUser(User user) {
        String email = user.getEmail().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }
        if (userRepository.findByPesel(user.getPesel()).isPresent()) {
            throw new UserAlreadyExistsException("PESEL: " + user.getPesel());
        }

        validatePesel(user.getPesel());
    }

    public void validateUserStatus(User user) {
        if (!user.isEnabled()) {
            throw new DisabledUserException(user.getUsername());
        }
    }

    private void validatePesel(String pesel) {
        if (pesel == null || pesel.isEmpty()) {
            throw new IllegalArgumentException("PESEL is required");
        }
        if (!pesel.matches("\\d{11}")) {
            throw new IllegalArgumentException("PESEL must be 11 digits");
        }
        if (!validatePeselChecksum(pesel)) {
            throw new IllegalArgumentException("Invalid PESEL checksum");
        }
    }

    private boolean validatePeselChecksum(String pesel) {
        int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
        int sum = 0;
        
        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(pesel.charAt(i));
            sum += (digit * weights[i]) % 10;
        }
        
        int checksum = (10 - (sum % 10)) % 10;
        int lastDigit = Character.getNumericValue(pesel.charAt(10));
        
        return checksum == lastDigit;
    }
}
