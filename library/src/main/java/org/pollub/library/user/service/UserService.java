package org.pollub.library.user.service;

import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.ApiTextResponse;
import org.pollub.library.exception.UserNotFoundException;
import org.pollub.library.user.model.Role;
import org.pollub.library.user.model.RoleSetDto;
import org.pollub.library.user.model.User;
import org.pollub.library.user.repository.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IUserContextService userContextService;

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
        userRepository.deleteById(id);
    }

}
