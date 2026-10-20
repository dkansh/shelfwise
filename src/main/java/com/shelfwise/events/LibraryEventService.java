package com.shelfwise.events;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.exception.DuplicateResourceException;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.LibraryBranch;
import com.shelfwise.repository.LibraryBranchRepository;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.security.SecurityUtils;
import com.shelfwise.service.NotificationService;

@Service
public class LibraryEventService {

    private final LibraryEventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final LibraryBranchRepository branchRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    public LibraryEventService(LibraryEventRepository eventRepository,
                               EventRegistrationRepository registrationRepository,
                               LibraryBranchRepository branchRepository,
                               MemberRepository memberRepository,
                               NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.branchRepository = branchRepository;
        this.memberRepository = memberRepository;
        this.notificationService = notificationService;
    }

    public List<LibraryEventDto> getUpcomingEvents() {
        return eventRepository
                .findByStartTimeAfterAndStatusOrderByStartTimeAsc(LocalDateTime.now(), EventStatus.SCHEDULED)
                .stream().map(this::toDto).toList();
    }

    public LibraryEventDto getEvent(Long id) {
        return toDto(findEvent(id));
    }

    @Transactional
    public LibraryEventDto createEvent(CreateEventRequest request) {
        LibraryBranch branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + request.branchId()));
        // TODO: check endTime is after startTime
        // TODO: check the room isn't already booked at that time

        LibraryEvent event = new LibraryEvent();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setBranch(branch);
        event.setCapacity(request.capacity());
        event.setStatus(EventStatus.SCHEDULED);
        event.setCreatedBy(SecurityUtils.currentUsername());
        return toDto(eventRepository.save(event));
    }

    @Transactional
    public EventRegistration register(Long eventId, Long memberId) {
        LibraryEvent event = findEvent(eventId);
        if (event.getStatus() != EventStatus.SCHEDULED) {
            throw new IllegalStateException("Event is not open for registration");
        }
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberId));

        boolean alreadyRegistered = registrationRepository.findByEventId(eventId).stream()
                .anyMatch(r -> r.getMemberId().equals(memberId));
        if (alreadyRegistered) {
            throw new DuplicateResourceException("Member is already registered for this event");
        }

        long registered = registrationRepository.countByEventId(eventId);
        if (registered >= event.getCapacity()) {
            // TODO: waiting list
            throw new IllegalStateException("Sorry, this event is full");
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setMemberId(memberId);
        registration.setRegisteredAt(LocalDateTime.now());
        registration.setAttended(false);
        registrationRepository.save(registration);

        notificationService.createInApp(memberId, "You're registered!",
                "See you at '" + event.getTitle() + "' on " + event.getStartTime());
        return registration;
    }

    public void cancelRegistration(Long eventId, Long memberId) {
        EventRegistration registration = registrationRepository.findByEventIdAndMemberId(eventId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        registrationRepository.delete(registration);
    }

    @Transactional
    public LibraryEventDto cancelEvent(Long eventId) {
        LibraryEvent event = findEvent(eventId);
        event.setStatus(EventStatus.CANCELLED);
        for (EventRegistration r : registrationRepository.findByEventId(eventId)) {
            notificationService.createInApp(r.getMemberId(), "Event cancelled",
                    "Sorry, '" + event.getTitle() + "' has been cancelled.");
        }
        return toDto(eventRepository.save(event));
    }

    public void markAttendance(Long eventId, Long memberId, boolean attended) {
        EventRegistration registration = registrationRepository.findByEventIdAndMemberId(eventId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        registration.setAttended(attended);
        registrationRepository.save(registration);
    }

    public List<EventRegistration> getRegistrations(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    private LibraryEvent findEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    private LibraryEventDto toDto(LibraryEvent e) {
        return new LibraryEventDto(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getStartTime(),
                e.getEndTime(),
                e.getBranch() != null ? e.getBranch().getName() : null,
                e.getCapacity(),
                registrationRepository.countByEventId(e.getId()),
                e.getStatus());
    }
}
