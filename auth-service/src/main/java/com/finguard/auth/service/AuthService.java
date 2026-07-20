package com.finguard.auth.service;

import com.finguard.auth.api.*;
import com.finguard.auth.domain.AppUser;
import com.finguard.auth.domain.UserRepository;
import com.finguard.auth.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}

    public AuthResponse login(LoginRequest request){
        AppUser user=users.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if(!encoder.matches(request.password(), user.getPasswordHash())) throw new BadCredentialsException("Invalid credentials");
        return response(user);
    }

    public AuthResponse register(RegisterRequest request){
        if(users.existsByEmailIgnoreCase(request.email())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Email is already registered");
        AppUser user=users.save(new AppUser(UUID.randomUUID(),request.email(),encoder.encode(request.password()),"CUSTOMER"));
        return response(user);
    }

    private AuthResponse response(AppUser user){
        return new AuthResponse(jwt.issue(user),"Bearer",jwt.expiresInSeconds(),user.getId(),user.getEmail(), List.of(user.getRole()));
    }
}
