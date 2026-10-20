package com.shelfwise.events;

import java.time.LocalDateTime;

import com.shelfwise.model.LibraryBranch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Something happening at the library - book clubs, author readings, kids story time etc.
 * (not to be confused with the spring application events in com.shelfwise.event)
 */
@Entity
@Table(name = "library_events")
@Getter
@Setter
public class LibraryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private LibraryBranch branch;

    private int capacity;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private String createdBy;
}
