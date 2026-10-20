package com.shelfwise.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shelfwise.exception.DuplicateResourceException;
import com.shelfwise.model.Member;
import com.shelfwise.repository.LibraryBranchRepository;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class LibraryEventServiceTest {

    @Mock
    private LibraryEventRepository eventRepository;

    @Mock
    private EventRegistrationRepository registrationRepository;

    @Mock
    private LibraryBranchRepository branchRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LibraryEventService eventService;

    @Test
    void register_success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event(10, EventStatus.SCHEDULED)));
        when(memberRepository.findById(7L)).thenReturn(Optional.of(new Member()));
        when(registrationRepository.countByEventId(1L)).thenReturn(3L);

        EventRegistration registration = eventService.register(1L, 7L);

        assertEquals(7L, registration.getMemberId());
        verify(registrationRepository).save(any(EventRegistration.class));
        verify(notificationService).createInApp(eq(7L), anyString(), anyString());
    }

    @Test
    void register_failsWhenEventIsFull() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event(10, EventStatus.SCHEDULED)));
        when(memberRepository.findById(7L)).thenReturn(Optional.of(new Member()));
        when(registrationRepository.countByEventId(1L)).thenReturn(10L);

        assertThrows(IllegalStateException.class, () -> eventService.register(1L, 7L));
    }

    @Test
    void register_failsWhenAlreadyRegistered() {
        EventRegistration existing = new EventRegistration();
        existing.setMemberId(7L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event(10, EventStatus.SCHEDULED)));
        when(memberRepository.findById(7L)).thenReturn(Optional.of(new Member()));
        when(registrationRepository.findByEventId(1L)).thenReturn(List.of(existing));

        assertThrows(DuplicateResourceException.class, () -> eventService.register(1L, 7L));
    }

    @Test
    void register_failsWhenEventCancelled() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event(10, EventStatus.CANCELLED)));

        assertThrows(IllegalStateException.class, () -> eventService.register(1L, 7L));
    }

    private LibraryEvent event(int capacity, EventStatus status) {
        LibraryEvent event = new LibraryEvent();
        event.setId(1L);
        event.setTitle("Book Club");
        event.setCapacity(capacity);
        event.setStatus(status);
        event.setStartTime(LocalDateTime.now().plusDays(2));
        return event;
    }
}
