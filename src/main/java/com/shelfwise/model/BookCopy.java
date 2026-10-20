package com.shelfwise.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A physical copy of a book sitting at one of the branches.
 */
@Entity
@Table(name = "book_copies")
@Getter
@Setter
@NoArgsConstructor
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private LibraryBranch branch;

    @Enumerated(EnumType.STRING)
    private CopyCondition condition;

    @Enumerated(EnumType.STRING)
    private CopyStatus status;

    private LocalDate acquiredDate;

    private BigDecimal price;

    private String notes;
}
