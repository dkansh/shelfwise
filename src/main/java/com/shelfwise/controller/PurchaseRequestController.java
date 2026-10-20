package com.shelfwise.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.CreatePurchaseRequest;
import com.shelfwise.model.PurchaseRequest;
import com.shelfwise.model.PurchaseRequestStatus;
import com.shelfwise.service.PurchaseRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/purchase-requests")
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    public PurchaseRequestController(PurchaseRequestService purchaseRequestService) {
        this.purchaseRequestService = purchaseRequestService;
    }

    @PostMapping
    public ResponseEntity<PurchaseRequest> submit(@Valid @RequestBody CreatePurchaseRequest request) {
        return new ResponseEntity<>(purchaseRequestService.submit(request), HttpStatus.CREATED);
    }

    @GetMapping
    public List<PurchaseRequest> list(@RequestParam(required = false) PurchaseRequestStatus status) {
        return purchaseRequestService.getByStatus(status);
    }

    @GetMapping("/member/{memberId}")
    public List<PurchaseRequest> forMember(@PathVariable Long memberId) {
        return purchaseRequestService.getForMember(memberId);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public PurchaseRequest updateStatus(@PathVariable Long id,
                                        @RequestParam PurchaseRequestStatus status,
                                        @RequestParam(required = false) String note) {
        return purchaseRequestService.updateStatus(id, status, note);
    }
}
