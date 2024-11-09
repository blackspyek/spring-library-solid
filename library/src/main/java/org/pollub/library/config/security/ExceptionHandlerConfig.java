package org.pollub.library.config.security;

import lombok.RequiredArgsConstructor;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ExceptionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ComponentScan(basePackages = "org.pollub.library.exception")
@RequiredArgsConstructor
public class ExceptionHandlerConfig {

    private Map<Class<? extends Exception>, ExceptionMapper> exceptionMappers;

    @Bean
    public Map<Class<? extends Exception>, ExceptionMapper> exceptionMappers() {
        return exceptionMappers;
    }
}
