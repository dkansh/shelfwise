package com.shelfwise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shelfwise.model.BookCopy;
import com.shelfwise.model.CopyStatus;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    Optional<BookCopy> findByBarcode(String barcode);

    List<BookCopy> findByBookId(Long bookId);

    List<BookCopy> findByBranchId(Long branchId);

    long countByBookIdAndStatus(Long bookId, CopyStatus status);
}
