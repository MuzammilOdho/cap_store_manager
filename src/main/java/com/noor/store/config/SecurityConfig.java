package com.noor.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Value("${app.security.user:admin}")
    private String username;

    @Value("${app.security.password:adminpass}")
    private String password;

    @Value("${app.security.role:ADMIN}")
    private String role;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(role)
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Security filter chain using the modern builder API.
     * Explicitly use AntPathRequestMatcher for the H2 console and swagger endpoints
     * to avoid ambiguity between multiple servlet mappings.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Build request matchers explicitly with Ant-style patterns for non-MVC endpoints
        AntPathRequestMatcher[] publicMatchers = new AntPathRequestMatcher[] {
                new AntPathRequestMatcher("/v3/api-docs/**"),
                new AntPathRequestMatcher("/swagger-ui/**"),
                new AntPathRequestMatcher("/swagger-ui.html"),
                new AntPathRequestMatcher("/h2-console/**")
        };

        http
                // For a single-user local app it's OK to disable CSRF; in production consider enabling it.
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Permit the explicitly declared public endpoints (Ant matchers)
                        .requestMatchers(publicMatchers).permitAll()
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // Basic auth for simplicity (token-based or form login can be added later)
                .httpBasic(Customizer.withDefaults());

        // Allow H2 console frames
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
