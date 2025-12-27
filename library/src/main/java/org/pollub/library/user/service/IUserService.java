package org.pollub.library.user.service;

import org.pollub.library.auth.model.ApiTextResponse;
import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.user.model.RoleSetDto;
import org.pollub.library.auth.model.ChangePasswordDto;
import org.pollub.library.user.model.User;

import java.util.List;

public interface IUserService {
    ApiTextResponse updateUserRoles(String username, RoleSetDto roles);
    User findByUsername(String username);
    User findByEmail(String email);
    User findById(Long id);
    void deleteUserById(Long id);
    User updateFavouriteBranch(String username, Long branchId);
    LibraryBranch getFavouriteBranch(String username);
    LibraryBranch getEmployeeBranch(String username);
    ApiTextResponse changePassword(String username, ChangePasswordDto passwordDto);
    List<User> findAll();
    List<User> searchUsers(String query);
}
