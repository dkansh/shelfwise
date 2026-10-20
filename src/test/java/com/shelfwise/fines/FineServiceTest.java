package com.shelfwise.fines;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shelfwise.model.Member;
import com.shelfwise.service.AuditService;

@ExtendWith(MockitoExtension.class)
class FineServiceTest {

    @Mock
    private FineRepository fineRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private FineService fineService;

    @Test
    void payFine_partialPayment() {
        Fine fine = fine("5.00", "0.00", FineStatus.UNPAID);
        when(fineRepository.findById(5L)).thenReturn(Optional.of(fine));
        when(fineRepository.save(any(Fine.class))).thenAnswer(inv -> inv.getArgument(0));

        FineDto result = fineService.payFine(5L, new PaymentRequest(new BigDecimal("2.00"), PaymentMethod.CASH, null));

        assertEquals(FineStatus.PARTIALLY_PAID, result.status());
        assertEquals(0, new BigDecimal("3.00").compareTo(result.remaining()));
    }

    @Test
    void payFine_fullPayment() {
        Fine fine = fine("5.00", "2.00", FineStatus.PARTIALLY_PAID);
        when(fineRepository.findById(5L)).thenReturn(Optional.of(fine));
        when(fineRepository.save(any(Fine.class))).thenAnswer(inv -> inv.getArgument(0));

        FineDto result = fineService.payFine(5L, new PaymentRequest(new BigDecimal("3.00"), PaymentMethod.CARD, "TX-1"));

        assertEquals(FineStatus.PAID, result.status());
    }

    @Test
    void payFine_alreadyPaid() {
        when(fineRepository.findById(5L)).thenReturn(Optional.of(fine("5.00", "5.00", FineStatus.PAID)));

        assertThrows(IllegalArgumentException.class,
                () -> fineService.payFine(5L, new PaymentRequest(BigDecimal.ONE, PaymentMethod.CASH, null)));
    }

    @Test
    void outstandingBalance_addsUpOpenFines() {
        when(fineRepository.findByMemberIdAndStatusIn(eq(1L), anyCollection())).thenReturn(List.of(
                fine("5.00", "2.00", FineStatus.PARTIALLY_PAID),
                fine("1.25", "0.00", FineStatus.UNPAID)));

        BigDecimal balance = fineService.getOutstandingBalance(1L);

        assertEquals(0, new BigDecimal("4.25").compareTo(balance));
    }

    private Fine fine(String amount, String paid, FineStatus status) {
        Member member = new Member();
        member.setId(1L);

        Fine fine = new Fine();
        fine.setId(5L);
        fine.setMember(member);
        fine.setAmount(new BigDecimal(amount));
        fine.setPaidAmount(new BigDecimal(paid));
        fine.setStatus(status);
        return fine;
    }
}
