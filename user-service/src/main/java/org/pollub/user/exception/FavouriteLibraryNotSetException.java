package org.pollub.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FavouriteLibraryNotSetException extends RuntimeException {
    public FavouriteLibraryNotSetException(Long userId) {
        super(String.format("Favourite library not set for user with id: %d", userId));
    }
}
