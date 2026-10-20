package com.shelfwise.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shelfwise.exception.InvalidStatusTransitionException;
import com.shelfwise.model.PurchaseRequest;
import com.shelfwise.model.PurchaseRequestStatus;
import com.shelfwise.repository.PurchaseRequestRepository;

@ExtendWith(MockitoExtension.class)
class PurchaseRequestServiceTest {

    @Mock
    private PurchaseRequestRepository repository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PurchaseRequestService service;

    @Test
    void approveSubmittedRequest() {
        when(repository.findById(1L)).thenReturn(Optional.of(request(PurchaseRequestStatus.SUBMITTED)));
        when(repository.save(any(PurchaseRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchaseRequest result = service.updateStatus(1L, PurchaseRequestStatus.APPROVED, "good idea");

        assertEquals(PurchaseRequestStatus.APPROVED, result.getStatus());
        assertEquals("good idea", result.getLibrarianNote());
        verify(notificationService).createInApp(eq(3L), anyString(), anyString());
    }

    @Test
    void cannotSkipStraightToReceived() {
        when(repository.findById(1L)).thenReturn(Optional.of(request(PurchaseRequestStatus.SUBMITTED)));

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateStatus(1L, PurchaseRequestStatus.RECEIVED, null));
    }

    @Test
    void rejectedRequestIsFinal() {
        when(repository.findById(1L)).thenReturn(Optional.of(request(PurchaseRequestStatus.REJECTED)));

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateStatus(1L, PurchaseRequestStatus.ORDERED, null));
    }

    private PurchaseRequest request(PurchaseRequestStatus status) {
        PurchaseRequest pr = new PurchaseRequest();
        pr.setId(1L);
        pr.setTitle("Refactoring");
        pr.setRequestedByMemberId(3L);
        pr.setStatus(status);
        return pr;
    }
}
