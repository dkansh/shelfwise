package com.shelfwise.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Moving a physical copy from one branch to another.
 */
@Entity
@Table(name = "copy_transfers")
@Getter
@Setter
public class CopyTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copy_id", nullable = false)
    private BookCopy copy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch_id")
    private LibraryBranch fromBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_branch_id")
    private LibraryBranch toBranch;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    private String requestedBy;

    private LocalDateTime requestedAt;

    private LocalDateTime shippedAt;

    private LocalDateTime receivedAt;
}
