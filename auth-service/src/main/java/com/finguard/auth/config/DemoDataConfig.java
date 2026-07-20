package com.finguard.auth.config;

import com.finguard.auth.domain.AppUser;
import com.finguard.auth.domain.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class DemoDataConfig {
    public static final UUID CUSTOMER_ID=UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID ADMIN_ID=UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Bean CommandLineRunner seedUsers(UserRepository users, PasswordEncoder encoder){
        return args -> {
            if(!users.existsById(CUSTOMER_ID)) users.save(new AppUser(CUSTOMER_ID,"customer@finguard.dev",encoder.encode("Password123!"),"CUSTOMER"));
            if(!users.existsById(ADMIN_ID)) users.save(new AppUser(ADMIN_ID,"admin@finguard.dev",encoder.encode("Admin123!"),"ADMIN"));
        };
    }
}
