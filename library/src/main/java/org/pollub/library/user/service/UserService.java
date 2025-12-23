package org.pollub.library.user.service;

import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.ApiTextResponse;
import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.branch.service.ILibraryBranchService;
import org.pollub.library.auth.model.ChangePasswordDto;
import org.pollub.library.exception.UserNotFoundException;
import org.pollub.library.user.model.Role;
import org.pollub.library.user.model.RoleSetDto;
import org.pollub.library.user.model.User;
import org.pollub.library.user.repository.IUserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IUserContextService userContextService;
    private final ILibraryBranchService branchService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ApiTextResponse updateUserRoles(String username, RoleSetDto roles) {
        User fetchedUser = userRepository.findByUsername(username)
                .map(user -> updateRoles(user, roles.getRoles()))
                .orElseThrow(() -> new UserNotFoundException(username));
        return new ApiTextResponse(true, "Roles updated successfully " + fetchedUser.getUsername());
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    private User updateRoles(User user, Set<Role> roles) {
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        if (userContextService.isThisSameUser(id)) {
            throw new IllegalArgumentException("You cannot delete yourself");
        }

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Nie można usunąć użytkownika, który posiada aktywną historię (wypożyczenia/rezerwacje).");
        }
    }
    @Override
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

        userRepository.save(user);
        return new ApiTextResponse(true, "Password for user " + username + " changed successfully");
    }
    @Override
    @Transactional
    public User updateFavouriteBranch(String username, Long branchId) {
        User user = findByUsername(username);
        if (branchId == null) {
            user.setFavouriteBranch(null);
        } else {
            LibraryBranch branch = branchService.getBranchById(branchId);
            user.setFavouriteBranch(branch);
        }
        return userRepository.save(user);
    }

    @Override
    public LibraryBranch getFavouriteBranch(String username) {
        User user = findByUsername(username);
        return user.getFavouriteBranch();
    }

    @Override
    @Transactional(readOnly = true)
    public LibraryBranch getEmployeeBranch(String username) {
        User user = userRepository.findByUsernameWithEmployeeBranch(username.toLowerCase())
                .orElseThrow(() -> new UserNotFoundException(username));
        return user.getEmployeeBranch();
    }

    private static final int MIN_SEARCH_QUERY_LENGTH = 3;
    private static final int MAX_SEARCH_RESULTS = 10;

    @Override
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return userRepository.searchUsers(query.trim(), PageRequest.of(0, 50));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
