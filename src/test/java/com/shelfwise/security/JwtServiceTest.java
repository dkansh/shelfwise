package com.shelfwise.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.shelfwise.model.AppUser;
import com.shelfwise.model.Role;

import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "test-secret-key-that-is-long-enough-for-hmac");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 60_000L);
    }

    @Test
    void tokenContainsUsernameAndRole() {
        String token = jwtService.generateToken(user("alice", Role.MEMBER));

        assertEquals("alice", jwtService.extractUsername(token));
        assertEquals("MEMBER", jwtService.extractRole(token));
    }

    @Test
    void tokenIsValidForSameUser() {
        String token = jwtService.generateToken(user("alice", Role.MEMBER));
        UserDetails alice = User.withUsername("alice").password("x").roles("MEMBER").build();
        UserDetails bob = User.withUsername("bob").password("x").roles("MEMBER").build();

        assertTrue(jwtService.isTokenValid(token, alice));
        assertFalse(jwtService.isTokenValid(token, bob));
    }

    @Test
    void tamperedTokenIsRejected() {
        String aliceToken = jwtService.generateToken(user("alice", Role.MEMBER));
        String adminToken = jwtService.generateToken(user("admin", Role.ADMIN));

        // admin payload with alice's signature
        String[] a = aliceToken.split("\\.");
        String[] b = adminToken.split("\\.");
        String forged = a[0] + "." + b[1] + "." + a[2];

        assertThrows(JwtException.class, () -> jwtService.extractUsername(forged));
    }

    private AppUser user(String username, Role role) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setRole(role);
        return user;
    }
}
