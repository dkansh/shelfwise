package com.shelfwise.fines;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.Loan;
import com.shelfwise.security.SecurityUtils;
import com.shelfwise.service.AuditService;

@Service
public class FineService {

    private static final List<FineStatus> OPEN_STATUSES = List.of(FineStatus.UNPAID, FineStatus.PARTIALLY_PAID);

    private final FineRepository fineRepository;
    private final PaymentRepository paymentRepository;
    private final AuditService auditService;

    public FineService(FineRepository fineRepository, PaymentRepository paymentRepository,
                       AuditService auditService) {
        this.fineRepository = fineRepository;
        this.paymentRepository = paymentRepository;
        this.auditService = auditService;
    }

    /**
     * Creates a fine record for a late loan. Called by LoanService when a book comes back late.
     */
    @Transactional
    public Fine createFineForLoan(Loan loan, double amount) {
        Fine fine = new Fine();
        fine.setLoan(loan);
        fine.setMember(loan.getMember());
        fine.setAmount(new BigDecimal(amount));
        fine.setPaidAmount(BigDecimal.ZERO);
        fine.setStatus(FineStatus.UNPAID);
        fine.setReason("Late return of '" + loan.getBook().getTitle() + "'");
        fine.setCreatedAt(LocalDateTime.now());
        return fineRepository.save(fine);
    }

    public List<FineDto> getFinesForMember(Long memberId) {
        return fineRepository.findByMemberId(memberId).stream().map(this::toDto).toList();
    }

    /**
     * Total amount the member still owes across all their unpaid / partially paid fines.
     */
    public BigDecimal getOutstandingBalance(Long memberId) {
        return fineRepository.findByMemberIdAndStatusIn(memberId, OPEN_STATUSES).stream()
                .map(f -> f.getAmount().subtract(f.getPaidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public FineDto payFine(Long fineId, PaymentRequest request) {
        Fine fine = getFine(fineId);
        if (fine.getStatus() == FineStatus.PAID || fine.getStatus() == FineStatus.WAIVED) {
            throw new IllegalArgumentException("Fine " + fineId + " is already settled");
        }

        Payment payment = new Payment();
        payment.setFine(fine);
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setReference(request.getReference());
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        fine.setPaidAmount(fine.getPaidAmount().add(request.getAmount()));
        if (fine.getPaidAmount().compareTo(fine.getAmount()) >= 0) {
            fine.setStatus(FineStatus.PAID);
        } else {
            fine.setStatus(FineStatus.PARTIALLY_PAID);
        }
        // TODO: what do we do if someone pays more than they owe? refund / store credit?

        return toDto(fineRepository.save(fine));
    }

    @Transactional
    public FineDto waiveFine(Long fineId, String reason) {
        Fine fine = getFine(fineId);
        fine.setStatus(FineStatus.WAIVED);
        fine.setWaivedBy(SecurityUtils.currentUsername());
        fine.setWaivedReason(reason);
        auditService.log("WAIVE_FINE", "Fine", fineId, reason);
        return toDto(fineRepository.save(fine));
    }

    public List<Payment> getPayments(Long fineId) {
        return paymentRepository.findByFineId(fineId);
    }

    private Fine getFine(Long fineId) {
        return fineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found: " + fineId));
    }

    private FineDto toDto(Fine fine) {
        return new FineDto(
                fine.getId(),
                fine.getLoan() != null ? fine.getLoan().getId() : null,
                fine.getMember().getId(),
                fine.getAmount(),
                fine.getPaidAmount(),
                fine.getAmount().subtract(fine.getPaidAmount()),
                fine.getStatus(),
                fine.getReason(),
                fine.getCreatedAt());
    }
}
