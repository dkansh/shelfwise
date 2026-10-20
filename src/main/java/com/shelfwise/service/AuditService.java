package com.shelfwise.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.shelfwise.model.AuditLog;
import com.shelfwise.repository.AuditLogRepository;
import com.shelfwise.security.SecurityUtils;

/**
 * Keeps a record of who did what. Call log() from anywhere that changes important data.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String entityType, Long entityId, String details) {
        AuditLog entry = new AuditLog();
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetails(details);
        entry.setUsername(SecurityUtils.currentUsername());
        entry.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(entry);
    }

    public Page<AuditLog> getRecent(int page, int size) {
        return auditLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
    }

    public List<AuditLog> getHistory(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }
}
