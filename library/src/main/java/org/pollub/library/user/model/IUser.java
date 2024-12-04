package org.pollub.library.user.model;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public interface IUser extends UserDetails {
    Long getId();
    String getEmail();
    Set<Role> getRoles();
}
