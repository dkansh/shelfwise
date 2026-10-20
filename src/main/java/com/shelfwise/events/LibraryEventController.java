package com.shelfwise.events;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/events")
public class LibraryEventController {

    private final LibraryEventService eventService;

    public LibraryEventController(LibraryEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<LibraryEventDto> upcoming() {
        return eventService.getUpcomingEvents();
    }

    @GetMapping("/{id}")
    public LibraryEventDto get(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<LibraryEventDto> create(@Valid @RequestBody CreateEventRequest request) {
        return new ResponseEntity<>(eventService.createEvent(request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<Map<String, Object>> register(@PathVariable Long id, @RequestParam Long memberId) {
        EventRegistration registration = eventService.register(id, memberId);
        return new ResponseEntity<>(Map.of(
                "registrationId", registration.getId(),
                "eventId", id,
                "memberId", memberId), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/register")
    public ResponseEntity<Void> unregister(@PathVariable Long id, @RequestParam Long memberId) {
        eventService.cancelRegistration(id, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public LibraryEventDto cancel(@PathVariable Long id) {
        return eventService.cancelEvent(id);
    }

    @GetMapping("/{id}/registrations")
    public List<EventRegistration> registrations(@PathVariable Long id) {
        return eventService.getRegistrations(id);
    }

    @PostMapping("/{id}/attendance")
    public ResponseEntity<Void> attendance(@PathVariable Long id, @RequestParam Long memberId,
                                           @RequestParam boolean attended) {
        eventService.markAttendance(id, memberId, attended);
        return ResponseEntity.ok().build();
    }
}
