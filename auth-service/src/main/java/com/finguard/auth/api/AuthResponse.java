package com.finguard.auth.api;

import java.util.List;
import java.util.UUID;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds, UUID userId, String email, List<String> roles) {}
