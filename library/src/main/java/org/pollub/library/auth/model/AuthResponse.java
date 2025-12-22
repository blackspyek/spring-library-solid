package org.pollub.library.auth.model;

import org.pollub.library.branch.model.LibraryBranch;
import org.pollub.library.user.model.Role;

public record AuthResponse(String token, Role[] roles, String responseType, String username, LibraryBranch employeeOfBranch) {
    private static final String RESPONSE_TYPE = "Bearer";

    public AuthResponse(String token, Role[] roles, String username) {
        this(token, roles, RESPONSE_TYPE, username, null);
    }

    public AuthResponse(String token, Role[] roles, String username, LibraryBranch employeeOfBranch) {
        this(token, roles, RESPONSE_TYPE, username, employeeOfBranch);
    }
}