package org.pollub.user.service;

import org.pollub.common.dto.BranchDto;
import org.pollub.user.dto.ApiTextResponse;
import org.pollub.user.dto.ChangePasswordDto;
import org.pollub.user.model.Role;
import org.pollub.user.model.User;

import java.util.List;
import java.util.Set;

public interface IUserService {

    User findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAll();

    List<User> searchUsers(String query);

    User createUser(User user);

    User updateUser(Long id, User updatedUser);

    User updateRoles(Long id, Set<Role> roles);

    User updateFavouriteBranch(String username, Long branchId);


    User updateEmployeeBranch(Long userId, Long branchId);

    Long getFavouriteBranchId(Long userId);

    Long getEmployeeBranchId(Long userId);

    BranchDto getEmployeeBranch(String username);

    List<User> findEmployeesByBranch(Long branchId);

    ApiTextResponse changePassword(String username, ChangePasswordDto passwordDto);

    User validateCredentials(String usernameOrEmail, String password);

    void deleteUser(Long id);
}
