package org.pollub.library.user.service;

import org.pollub.library.auth.model.ApiTextResponse;
import org.pollub.library.user.model.RoleSetDto;
import org.pollub.library.user.model.User;

public interface IUserService {
    ApiTextResponse updateUserRoles(String username, RoleSetDto roles);
    User findByUsername(String username);
    User findById(Long id);
}
