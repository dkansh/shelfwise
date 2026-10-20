package com.shelfwise.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.shelfwise.model.AppUser;
import com.shelfwise.model.Member;
import com.shelfwise.model.Role;
import com.shelfwise.repository.AppUserRepository;
import com.shelfwise.repository.MemberRepository;

/**
 * Creates the default logins. Runs after DataSeeder so the members exist.
 */
@Component
@Order(2)
public class UserSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);

    private final AppUserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(AppUserRepository userRepository, MemberRepository memberRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        createUser("admin", "admin123", "admin@shelfwise.local", Role.ADMIN, null);
        createUser("librarian", "librarian123", "librarian@shelfwise.local", Role.LIBRARIAN, null);
        createUser("alice", "alice123", "alice@example.com", Role.MEMBER, memberRepository.findByEmail("alice@example.com"));
        createUser("bob", "bob123", "bob@example.com", Role.MEMBER, memberRepository.findByEmail("bob@example.com"));

        log.warn("Default users created (admin/admin123, librarian/librarian123, ...) - change these passwords!");
    }

    private void createUser(String username, String password, String email, Role role, Member member) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(role);
        user.setMemberId(member != null ? member.getId() : null);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
