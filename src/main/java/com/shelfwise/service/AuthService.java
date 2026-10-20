package com.shelfwise.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.dto.AuthResponse;
import com.shelfwise.dto.LoginRequest;
import com.shelfwise.dto.RegisterRequest;
import com.shelfwise.exception.DuplicateResourceException;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.AppUser;
import com.shelfwise.model.Member;
import com.shelfwise.model.MembershipType;
import com.shelfwise.model.Role;
import com.shelfwise.repository.AppUserRepository;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.security.JwtService;
import com.shelfwise.security.SecurityUtils;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepository,
                       MemberRepository memberRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new member and logs them straight in.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username '" + request.username() + "' is already taken");
        }
        // TODO: check the email isn't already used by another member

        Member member = new Member();
        member.setName(request.name());
        member.setEmail(request.email());
        member.setPhone(request.phone());
        member.setMembershipType(MembershipType.STANDARD);
        member.setJoinedDate(new Date());
        member.setMembershipExpiry(LocalDate.now().plusYears(1));
        member.setActive(true);
        memberRepository.save(member);

        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRole(Role.MEMBER);
        user.setMemberId(member.getId());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        return toResponse(user, jwtService.generateToken(user));
    }

    public AuthResponse login(LoginRequest request) {
        // throws BadCredentialsException if the password is wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        AppUser user = userRepository.findByUsername(request.username()).get();
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return toResponse(user, jwtService.generateToken(user));
    }

    public AppUser getCurrentUser() {
        String username = SecurityUtils.currentUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    /**
     * Returns the member id of the logged in user (null for staff accounts).
     */
    public Long getCurrentMemberId() {
        return getCurrentUser().getMemberId();
    }

    public void changePassword(String oldPassword, String newPassword) {
        AppUser user = getCurrentUser();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is wrong");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private AuthResponse toResponse(AppUser user, String token) {
        return new AuthResponse(token, "Bearer", user.getUsername(), user.getRole().name(),
                jwtService.getExpirationMs() / 1000);
    }
}
