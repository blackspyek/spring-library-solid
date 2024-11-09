package org.pollub.library.user.service;

import lombok.RequiredArgsConstructor;
import org.pollub.library.auth.model.ApiResponse;
import org.pollub.library.exception.UserNotFoundException;
import org.pollub.library.user.model.Role;
import org.pollub.library.user.model.RoleSetDto;
import org.pollub.library.user.model.User;
import org.pollub.library.user.IUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public ApiResponse updateUserRoles(String username, RoleSetDto roles) {
        User fetchedUser = userRepository.findByUsername(username)
                .map(user -> updateRoles(user, roles.getRoles()))
                .orElseThrow(() -> new UserNotFoundException(username));
        return new ApiResponse(true, "Roles updated successfully " + fetchedUser.getUsername());
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UserNotFoundException(username));
    }
    private User updateRoles(User user, Set<Role> roles) {
        user.setRoles(roles);
        return userRepository.save(user);
    }
}
