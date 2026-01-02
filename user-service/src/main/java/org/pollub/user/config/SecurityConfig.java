package org.pollub.user.config;

import org.pollub.common.security.JwtAuthFilter;
import org.pollub.common.security.JwtTokenProvider;
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
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @org.springframework.beans.factory.annotation.Autowired
    private org.pollub.common.security.JwtAuthenticationEntryPoint authEntryPoint;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(jwtSecret);
    }
    
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtTokenProvider());
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
                .requestMatchers(HttpMethod.POST, "/api/users/validate").permitAll() // Used by Auth Service publicly before Login? No, backend-to-backend.
                // Wait, /validate and /exists are used by other services. Now they will have Internal Auth header.
                // So we can remove permitAll() and rely on InternalAuthFilter setting authentication.
                // But we must support user registration by public users.
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // Registration
                .requestMatchers(HttpMethod.POST, "/api/users/reset-password").permitAll() // Password reset (called by auth-service)
                .requestMatchers("/actuator/**").permitAll()
                // All other endpoints require authentication (JWT or Internal Token)
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
