package com.shelfwise.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.LoanRequest;
import com.shelfwise.dto.LoanResponse;
import com.shelfwise.model.Loan;
import com.shelfwise.service.AuthService;
import com.shelfwise.service.LoanService;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;
    private final AuthService authService;

    public LoanController(LoanService loanService, AuthService authService) {
        this.loanService = loanService;
        this.authService = authService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody LoanRequest request) {
        try {
            LoanResponse response = loanService.checkoutBook(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Checkout failed", e);
            // TODO: return a proper ErrorResponse like the other endpoints
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/return")
    public LoanResponse returnBook(@PathVariable("id") Long loanId) {
        return loanService.returnBook(loanId);
    }

    @PostMapping("/{id}/renew")
    public LoanResponse renewLoan(@PathVariable Long id) {
        return loanService.renewLoan(id);
    }

    @GetMapping("/me")
    public List<LoanResponse> myLoans() {
        return loanService.getLoansForMember(authService.getCurrentMemberId());
    }

    @GetMapping("/member/{memberId}")
    public List<LoanResponse> getLoansForMember(@PathVariable Long memberId) {
        return loanService.getLoansForMember(memberId);
    }

    @GetMapping("/overdue")
    public List<LoanResponse> getOverdueLoans() {
        return loanService.getOverdueLoans();
    }

    @GetMapping("/{id}/fine")
    public Map<String, Object> getFine(@PathVariable Long id) {
        Loan loan = loanService.getLoan(id);
        return Map.of("loanId", id, "fine", loanService.calculateFine(loan));
    }
}
