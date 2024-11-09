package org.pollub.library.user.service;

import org.pollub.library.auth.model.ApiResponse;
import org.pollub.library.user.model.Role;
import org.pollub.library.user.model.RoleSetDto;
import org.pollub.library.user.model.User;

import java.util.Set;

public interface IUserService {
    ApiResponse updateUserRoles(String username, RoleSetDto roles);
    User findByUsername(String username);
}
