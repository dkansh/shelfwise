package com.shelfwise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Self sign-up for new members. Creates both the Member and the login account.
 */
public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String name,
        @Email String email,
        String phone) {
}
