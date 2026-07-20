package com.finguard.auth.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@Email @NotBlank String email, @NotBlank @Size(min=10,max=100) String password) {}
