package com.finguard.auth.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_users", uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"))
public class AppUser {
    @Id
    private UUID id;
    @Column(nullable = false, length = 160)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    @Column(nullable = false, length = 30)
    private String role;
    @Column(nullable = false)
    private Instant createdAt;

    protected AppUser() {}
    public AppUser(UUID id, String email, String passwordHash, String role) {
        this.id=id; this.email=email.toLowerCase(); this.passwordHash=passwordHash; this.role=role; this.createdAt=Instant.now();
    }
    public UUID getId(){return id;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;}
    public String getRole(){return role;} public Instant getCreatedAt(){return createdAt;}
}
