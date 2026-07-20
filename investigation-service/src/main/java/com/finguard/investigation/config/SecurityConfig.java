package com.finguard.investigation.config;

import com.finguard.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;

@Configuration
@EnableMethodSecurity
public class SecurityConfig extends SecuritySupport {
    @Bean
    JwtTokenVerifier jwtTokenVerifier(@Value("${security.jwt.secret}") String secret) {
        return new JwtTokenVerifier(secret);
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenVerifier verifier) {
        return new JwtAuthenticationFilter(verifier);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter filter) throws Exception {
        return secure(http, filter, "/actuator/**", "/v3/api-docs/**",
                "/swagger-ui/**", "/swagger-ui.html");
    }

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
