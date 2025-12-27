package org.pollub.library.config.security;


import lombok.RequiredArgsConstructor;
import org.pollub.library.config.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").hasAnyRole("ADMIN", "LIBRARIAN") // (Wymóg 1 spełniony tutaj)
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

                        .requestMatchers(HttpMethod.PUT, "/api/user/roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/user").hasAnyRole("ADMIN", "LIBRARIAN") // NOWE: Lista użytkowników (Wymóg 3)
                        .requestMatchers(HttpMethod.GET, "/api/user/username/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/user/email/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/user/id/**").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/api/user/**").hasRole("ADMIN") // ZMIANA: Tylko ADMIN (Wymóg 2)

                        .requestMatchers(HttpMethod.PUT, "/api/user/password").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/user/favourite-branch").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/user/favourite-branch").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/user/employee-branch").hasAnyRole("ADMIN", "LIBRARIAN")
                        .requestMatchers(HttpMethod.GET, "/api/user/search").hasAnyRole("ADMIN", "LIBRARIAN")

                    .requestMatchers(HttpMethod.GET, "/api/rentals/user/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/rentals/available").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/rentals/available/branch/**").hasAnyRole("ADMIN", "LIBRARIAN")
                    .requestMatchers(HttpMethod.POST, "/api/rentals/rent").hasAnyRole("ADMIN", "LIBRARIAN")
                    .requestMatchers(HttpMethod.POST, "/api/rentals/return/**").hasAnyRole("ADMIN", "LIBRARIAN")
                    .requestMatchers(HttpMethod.GET, "/api/rentals/history/recent").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/rentals/history/export").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/rentals/all-rented").hasAnyRole("ADMIN", "LIBRARIAN")
                    .requestMatchers(HttpMethod.POST, "/api/rentals/extend/**").authenticated()

                    .requestMatchers(HttpMethod.GET, "/api/book/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/book").hasAnyRole("ADMIN", "LIBRARIAN")
                    .requestMatchers(HttpMethod.PUT, "/api/book/**").hasAnyRole("ADMIN", "LIBRARIAN")
                    .requestMatchers(HttpMethod.DELETE, "/api/book/**").hasAnyRole("ADMIN", "LIBRARIAN")

                    .requestMatchers(HttpMethod.POST, "/api/reservations").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/reservations/my").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/reservations/count").authenticated()

                    .requestMatchers(HttpMethod.POST, "/api/feedback").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/feedback").hasAnyRole("ADMIN", "LIBRARIAN")
                    .requestMatchers(HttpMethod.PUT, "/api/feedback/**").hasAnyRole("ADMIN", "LIBRARIAN")

                    .requestMatchers(HttpMethod.GET, "/api/branches/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/branches").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/branches/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/branches/**").hasRole("ADMIN")
                    .anyRequest().denyAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        return http.build();

    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://158.101.222.222"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
