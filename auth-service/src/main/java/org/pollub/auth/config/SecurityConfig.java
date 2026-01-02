package org.pollub.auth.config;

import org.pollub.auth.security.JwtTokenProvider;
import org.pollub.common.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final org.pollub.common.security.JwtAuthenticationEntryPoint authEntryPoint;
    
    public SecurityConfig(JwtTokenProvider jwtTokenProvider, 
                          org.pollub.common.security.JwtAuthenticationEntryPoint authEntryPoint) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        // Use common-lib JwtAuthFilter with a wrapper around local JwtTokenProvider
        return new JwtAuthFilter(new org.pollub.common.security.JwtTokenProvider(jwtTokenProvider.getSecret()));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
                .requestMatchers("/api/auth/validate").permitAll()
                .requestMatchers("/api/auth/health").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                // Register requires authentication (role check via @PreAuthorize)
                .requestMatchers(HttpMethod.POST, "/api/auth/register").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(internalAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Value("${internal.secret}")
    private String internalSecret;
    
    @Bean
    public org.pollub.common.security.InternalAuthFilter internalAuthFilter() {
        return new org.pollub.common.security.InternalAuthFilter(internalSecret);
    }
}
