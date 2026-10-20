package com.shelfwise.fines;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/fines")
@Tag(name = "Fines", description = "Late fees and payments")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "All fines for a member")
    public List<FineDto> getFines(@PathVariable Long memberId) {
        return fineService.getFinesForMember(memberId);
    }

    @GetMapping("/member/{memberId}/balance")
    @Operation(summary = "How much the member still owes")
    public Map<String, Object> getBalance(@PathVariable Long memberId) {
        return Map.of("memberId", memberId, "balance", fineService.getOutstandingBalance(memberId));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Pay (part of) a fine")
    public FineDto pay(@PathVariable Long id, @RequestBody PaymentRequest request) {
        return fineService.payFine(id, request);
    }

    @GetMapping("/{id}/payments")
    public List<Payment> payments(@PathVariable Long id) {
        return fineService.getPayments(id);
    }

    @PostMapping("/{id}/waive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancel a fine (admin only)")
    public FineDto waive(@PathVariable Long id, @RequestParam String reason) {
        return fineService.waiveFine(id, reason);
    }
}
