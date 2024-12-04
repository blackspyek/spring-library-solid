package org.pollub.library.user.service;

import org.pollub.library.user.model.IUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserContextService implements IUserContextService {
    @Override
    public Optional<Long> getOptionalCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return Optional.empty();
        }

        IUser principal = (IUser) authentication.getPrincipal();
        return Optional.of(principal.getId());

    }

    public boolean isThisSameUser(Long id) {
        return getOptionalCurrentUserId().orElseThrow(
                () -> new IllegalStateException("User not authenticated")
        ).equals(id);
    }
}
