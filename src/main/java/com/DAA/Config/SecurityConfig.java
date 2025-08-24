package com.DAA.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class SecurityConfig {

    private final JWTFilter jwtFilter;

    public SecurityConfig(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable();

        http.addFilterBefore(jwtFilter, AuthorizationFilter.class);

        http.authorizeHttpRequests()
//                // Public endpoints
//                .requestMatchers("/auth/doctor/signup", "/auth/doctor/login",
//                        "/auth/patient/signup", "/auth/patient/login",
//                        "/doctor/search/**")
//                .permitAll()
//
//                // Admin-only
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//
//                // Doctor-only
//                .requestMatchers("/doctor/**").hasRole("DOCTOR")
//
//                // Patient-only
//                .requestMatchers("/appointments/book", "/appointments/patient/**").hasRole("PATIENT")
//
//                // Everything else
                .anyRequest().permitAll();

        return http.build();
    }
}

