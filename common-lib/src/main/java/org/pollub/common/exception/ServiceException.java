package org.pollub.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a service call fails.
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceException extends RuntimeException {
    
    private final String serviceName;
    
    public ServiceException(String serviceName, String message) {
        super(String.format("Error calling %s: %s", serviceName, message));
        this.serviceName = serviceName;
    }
    
    public ServiceException(String serviceName, String message, Throwable cause) {
        super(String.format("Error calling %s: %s", serviceName, message), cause);
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
}
