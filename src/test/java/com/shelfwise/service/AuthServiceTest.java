package com.shelfwise.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shelfwise.dto.AuthResponse;
import com.shelfwise.dto.RegisterRequest;
import com.shelfwise.exception.DuplicateResourceException;
import com.shelfwise.model.AppUser;
import com.shelfwise.model.Member;
import com.shelfwise.model.Role;
import com.shelfwise.repository.AppUserRepository;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_failsWhenUsernameTaken() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("alice", "password", "Alice", "alice@example.com", null);
        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(memberRepository, never()).save(any());
    }

    @Test
    void register_createsMemberAndLinkedUser() {
        when(userRepository.existsByUsername("zoe")).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> {
            Member m = inv.getArgument(0);
            m.setId(42L);
            return m;
        });
        when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");
        when(jwtService.generateToken(any(AppUser.class))).thenReturn("token-123");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        AuthResponse response = authService.register(
                new RegisterRequest("zoe", "secret", "Zoe", "zoe@example.com", "555-1234"));

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(captor.capture());
        AppUser saved = captor.getValue();

        assertEquals("hashed-secret", saved.getPassword());
        assertEquals(42L, saved.getMemberId());
        assertEquals(Role.MEMBER, saved.getRole());
        assertEquals("token-123", response.token());
        assertEquals(3600, response.expiresIn());
    }
}
