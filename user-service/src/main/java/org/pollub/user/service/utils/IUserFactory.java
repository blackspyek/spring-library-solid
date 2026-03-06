package org.pollub.user.service.utils;

import org.pollub.user.model.User;

//Lab1 - Factory 3 Method Start
public interface IUserFactory {
    User createUser(User userDto);
    User createUser(String username, String email, String password, String name, String surname);
}
//Lab1 End Factory 3 Method
