package org.pollub.library.user.service;

import java.util.Optional;

public interface IUserContextService {
    Optional<Long> getOptionalCurrentUserId();
    boolean isThisSameUser(Long id);
}