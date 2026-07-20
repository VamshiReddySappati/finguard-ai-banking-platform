package com.finguard.auth.config;

import com.finguard.security.JwtAuthenticationFilter;
import com.finguard.security.JwtTokenVerifier;
import com.finguard.security.SecuritySupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig extends SecuritySupport {
    @Bean JwtTokenVerifier jwtTokenVerifier(@Value("${security.jwt.secret}") String secret){return new JwtTokenVerifier(secret);}
    @Bean JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenVerifier verifier){return new JwtAuthenticationFilter(verifier);}
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter filter) throws Exception {
        return secure(http,filter,"/api/auth/**","/actuator/**","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html");
    }
}
