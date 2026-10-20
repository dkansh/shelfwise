package com.shelfwise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.DashboardStats;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.AppUser;
import com.shelfwise.model.AuditLog;
import com.shelfwise.model.Role;
import com.shelfwise.repository.AppUserRepository;
import com.shelfwise.service.AuditService;
import com.shelfwise.service.ReportService;

/**
 * Admin only stuff (secured in SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/users")
    public List<AppUser> getUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/users/{id}/role")
    public AppUser changeRole(@PathVariable Long id, @RequestParam Role role) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setRole(role);
        auditService.log("CHANGE_ROLE", "AppUser", id, "new role " + role);
        return userRepository.save(user);
    }

    @PutMapping("/users/{id}/enabled")
    public AppUser setEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    @GetMapping("/audit")
    public Page<AuditLog> audit(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "50") int size) {
        return auditService.getRecent(page, size);
    }

    @GetMapping("/audit/{entityType}/{entityId}")
    public List<AuditLog> auditForEntity(@PathVariable String entityType, @PathVariable Long entityId) {
        return auditService.getHistory(entityType, entityId);
    }

    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCaches() {
        for (String name : cacheManager.getCacheNames()) {
            cacheManager.getCache(name).clear();
        }
        return ResponseEntity.ok("Caches cleared");
    }

    @GetMapping("/stats")
    public DashboardStats stats() {
        return reportService.getDashboardStats();
    }
}
