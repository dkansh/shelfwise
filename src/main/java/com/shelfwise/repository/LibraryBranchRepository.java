package com.shelfwise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shelfwise.model.LibraryBranch;

public interface LibraryBranchRepository extends JpaRepository<LibraryBranch, Long> {

    Optional<LibraryBranch> findByCode(String code);

    List<LibraryBranch> findByActiveTrue();
}
