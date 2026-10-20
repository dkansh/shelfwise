package com.shelfwise.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.dto.CreatePurchaseRequest;
import com.shelfwise.exception.InvalidStatusTransitionException;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.PurchaseRequest;
import com.shelfwise.model.PurchaseRequestStatus;
import com.shelfwise.repository.PurchaseRequestRepository;

/**
 * Members can ask the library to buy books. Librarians approve or reject them.
 */
@Service
public class PurchaseRequestService {

    private static final Map<PurchaseRequestStatus, Set<PurchaseRequestStatus>> ALLOWED_TRANSITIONS = Map.of(
            PurchaseRequestStatus.SUBMITTED, Set.of(PurchaseRequestStatus.APPROVED, PurchaseRequestStatus.REJECTED),
            PurchaseRequestStatus.APPROVED, Set.of(PurchaseRequestStatus.ORDERED, PurchaseRequestStatus.REJECTED),
            PurchaseRequestStatus.ORDERED, Set.of(PurchaseRequestStatus.RECEIVED),
            PurchaseRequestStatus.REJECTED, Set.of(),
            PurchaseRequestStatus.RECEIVED, Set.of());

    private final PurchaseRequestRepository repository;
    private final NotificationService notificationService;

    public PurchaseRequestService(PurchaseRequestRepository repository, NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    public PurchaseRequest submit(CreatePurchaseRequest request) {
        // TODO: check if the library already has this book / if someone already asked for it
        PurchaseRequest pr = new PurchaseRequest();
        pr.setTitle(request.title());
        pr.setAuthor(request.author());
        pr.setIsbn(request.isbn());
        pr.setRequestedByMemberId(request.memberId());
        pr.setReason(request.reason());
        pr.setStatus(PurchaseRequestStatus.SUBMITTED);
        pr.setCreatedAt(LocalDateTime.now());
        pr.setUpdatedAt(LocalDateTime.now());
        return repository.save(pr);
    }

    @Transactional
    public PurchaseRequest updateStatus(Long id, PurchaseRequestStatus newStatus, String note) {
        PurchaseRequest pr = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase request not found: " + id));

        if (!ALLOWED_TRANSITIONS.get(pr.getStatus()).contains(newStatus)) {
            throw new InvalidStatusTransitionException(pr.getStatus().name(), newStatus.name());
        }

        pr.setStatus(newStatus);
        pr.setLibrarianNote(note);
        pr.setUpdatedAt(LocalDateTime.now());
        PurchaseRequest saved = repository.save(pr);

        notificationService.createInApp(pr.getRequestedByMemberId(), "Update on your book request",
                "Your request for '" + pr.getTitle() + "' is now " + newStatus);

        // TODO: when RECEIVED, create the Book automatically
        return saved;
    }

    public List<PurchaseRequest> getByStatus(PurchaseRequestStatus status) {
        if (status == null) {
            return repository.findAll();
        }
        return repository.findByStatusOrderByCreatedAtAsc(status);
    }

    public List<PurchaseRequest> getForMember(Long memberId) {
        return repository.findByRequestedByMemberId(memberId);
    }
}
