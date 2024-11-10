package org.pollub.library.user.model;

import lombok.Data;
import org.pollub.library.utils.ValidRoleSet;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RoleSetDto {
    @ValidRoleSet()
    private Set<String> roles;

    public Set<Role> getRoles(){
        return roles.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
