package org.pollub.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.common.dto.BranchDto;
import org.pollub.common.dto.UserAddressDto;
import org.pollub.common.exception.FavouriteLibraryNotSetException;
import org.pollub.common.exception.ResourceNotFoundException;
import org.pollub.common.exception.UserNotFoundException;
import org.pollub.user.client.BranchServiceClient;
import org.pollub.user.dto.ApiTextResponse;
import org.pollub.user.dto.ChangePasswordDto;
import org.pollub.user.dto.ResetPasswordRequestDto;
import org.pollub.user.dto.ResetPasswordResponseDto;
import org.pollub.user.model.Role;
import org.pollub.user.model.User;
import org.pollub.user.model.UserAddress;
import org.pollub.user.repository.IUserRepository;
import org.pollub.user.service.utils.UserFactory;
import org.pollub.user.service.utils.UserValidator;
import org.pollub.user.util.PasswordGenerator;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements  IUserService {
    
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory userFactory;
    private final UserValidator userValidator;
    private final BranchServiceClient branchServiceClient;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.searchUsers(query.trim(), PageRequest.of(0, 50));
    }

    @Transactional
    public User createUser(User user) {
        userValidator.validateNewUser(user);

        User createdUser = userFactory.createUser(
                user
        );
        return userRepository.save(createdUser);
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        User user = findById(id);
        user.setName(updatedUser.getName());
        user.setSurname(updatedUser.getSurname());
        user.setPhone(updatedUser.getPhone());
        return userRepository.save(user);
    }

    @Transactional
    public User updateAddress(Long id, UserAddressDto addressDto) {
        User user = findById(id);
        UserAddress address = UserAddress.builder()
                .street(addressDto.getStreet())
                .city(addressDto.getCity())
                .postalCode(addressDto.getPostalCode())
                .country(addressDto.getCountry())
                .buildingNumber(addressDto.getBuildingNumber())
                .apartmentNumber(addressDto.getApartmentNumber())
                .build();
        user.setAddress(address);
        return userRepository.save(user);
    }

    @Transactional
    public User updateRoles(Long id, Set<Role> roles) {
        User user = findById(id);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional
    public User updateFavouriteBranch(String username, Long branchId) {
        User user = findByUsername(username);
        user.setFavouriteBranchId(branchId);
        return userRepository.save(user);
    }

    @Transactional
    public User updateEmployeeBranch(Long userId, Long branchId) {
        User user = findById(userId);
        user.setEmployeeBranchId(branchId);
        return userRepository.save(user);
    }

    public Long getFavouriteBranchId(Long userId) {
        User user = findById(userId);
        return user.getFavouriteBranchId();
    }

    public Long getEmployeeBranchId(Long userId) {
        User user = findById(userId);
        return user.getEmployeeBranchId();
    }

    public BranchDto getEmployeeBranch(String username) {
        User user = findByUsername(username);
        Long branchId = user.getEmployeeBranchId();
        
        if (branchId == null) {
            return null;
        }
        
        return branchServiceClient.getBranchById(branchId)
                .orElse(null);
    }
    
    /**
     * Get user's favourite branch with full details from branch-service.
     */
    public BranchDto getFavouriteBranch(Long userId) {
        User user = findById(userId);
        Long branchId = user.getFavouriteBranchId();

        if (branchId == null) {
            log.error("User with id {} does not have a favourite library set", userId);
            throw new FavouriteLibraryNotSetException(
                    userId
            );
        }
        
        return branchServiceClient.getBranchById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Biblioteka o id " + branchId + " nie została znaleziona"));
    }
    
    /**
     * Get user's employee branch with full details from branch-service.
     */
    public BranchDto getEmployeeBranchById(Long userId) {
        User user = findById(userId);
        Long branchId = user.getEmployeeBranchId();
        
        if (branchId == null) {
            throw new ResourceNotFoundException("Użytkownik nie jest przypisany do żadnej biblioteki jako pracownik");
        }
        
        return branchServiceClient.getBranchById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Biblioteka o id " + branchId + " nie została znaleziona"));
    }

    public List<User> findEmployeesByBranch(Long branchId) {
        return userRepository.findByEmployeeBranchId(branchId);
    }

    @Transactional
    public ApiTextResponse changePassword(String username, ChangePasswordDto passwordDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (passwordEncoder.matches(passwordDto.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Nowe hasło nie może być takie samo jak dotychczasowe.");
        }

        if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Błąd przy zmianie hasła. Proszę zweryfikuj wpisane hasło.");
        }

        String encodedPassword = passwordEncoder.encode(passwordDto.getNewPassword());
        user.setPassword(encodedPassword);
        user.setMustChangePassword(false);  // User changed password after first login

        userRepository.save(user);
        return new ApiTextResponse(true, "Password for user " + username + " changed successfully");
    }

    /**
     * Validate user credentials for authentication
     */
    public User validateCredentials(String usernameOrEmail, String password) {
        User user = userRepository.findByEmail(usernameOrEmail)
                .or(() -> userRepository.findByUsername(usernameOrEmail.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Reset password for a user identified by email and PESEL.
     * Generates a new temporary password, sets mustChangePassword to true, and returns the new password.
     */
    @Transactional
    public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto request) {
        // Find user by email and PESEL
        User user = userRepository.findByEmailAndPesel(request.getEmail(), request.getPesel())
                .orElse(null);
        
        // Always return success message for security (don't reveal if user exists)
        if (user == null) {
            return ResetPasswordResponseDto.builder()
                    .success(false)
                    .message("Jeśli podane dane są poprawne, nowe hasło zostanie wysłane na podany adres email.")
                    .build();
        }
        
        // Generate new temporary password
        String temporaryPassword = PasswordGenerator.generatePassword();
        
        // Encode and save new password
        String encodedPassword = passwordEncoder.encode(temporaryPassword);
        user.setPassword(encodedPassword);
        user.setMustChangePassword(true);
        userRepository.save(user);
        
        return ResetPasswordResponseDto.builder()
                .email(user.getEmail())
                .temporaryPassword(temporaryPassword)
                .success(true)
                .message("Nowe hasło zostało wygenerowane i zostanie wysłane na podany adres email.")
                .build();
    }
}
