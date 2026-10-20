package com.shelfwise.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * A member asking the library to buy a book.
 */
@Data
@Entity
@Table(name = "purchase_requests")
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private String isbn;

    private Long requestedByMemberId;

    @Column(length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    private PurchaseRequestStatus status;

    private String librarianNote;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
