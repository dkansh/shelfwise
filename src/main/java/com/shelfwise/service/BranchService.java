package com.shelfwise.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.LibraryBranch;
import com.shelfwise.repository.LibraryBranchRepository;

@Service
public class BranchService {

    private final LibraryBranchRepository branchRepository;

    public BranchService(LibraryBranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<LibraryBranch> getActiveBranches() {
        return branchRepository.findAll();
    }

    public LibraryBranch getBranch(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + id));
    }

    public LibraryBranch createBranch(LibraryBranch branch) {
        if (branchRepository.findByCode(branch.getCode()).isPresent()) {
            throw new IllegalArgumentException("Branch code " + branch.getCode() + " is already used");
        }
        return branchRepository.save(branch);
    }

    public LibraryBranch updateBranch(Long id, LibraryBranch updated) {
        LibraryBranch existing = getBranch(id);
        if (!existing.getActive()) {
            throw new IllegalStateException("Can't update a closed branch");
        }
        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());
        existing.setPhone(updated.getPhone());
        existing.setOpeningHours(updated.getOpeningHours());
        return branchRepository.save(existing);
    }

    public void closeBranch(Long id) {
        LibraryBranch branch = getBranch(id);
        branch.setActive(false);
        // TODO: what happens to the copies and events at this branch?
        branchRepository.save(branch);
    }
}
