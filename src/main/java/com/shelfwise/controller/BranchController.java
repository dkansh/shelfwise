package com.shelfwise.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.model.LibraryBranch;
import com.shelfwise.service.BranchService;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public List<LibraryBranch> getBranches() {
        return branchService.getActiveBranches();
    }

    @GetMapping("/{id}")
    public LibraryBranch getBranch(@PathVariable Long id) {
        return branchService.getBranch(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibraryBranch> create(@RequestBody LibraryBranch branch) {
        return new ResponseEntity<>(branchService.createBranch(branch), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public LibraryBranch update(@PathVariable Long id, @RequestBody LibraryBranch branch) {
        return branchService.updateBranch(id, branch);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> close(@PathVariable Long id) {
        branchService.closeBranch(id);
        return ResponseEntity.noContent().build();
    }
}
