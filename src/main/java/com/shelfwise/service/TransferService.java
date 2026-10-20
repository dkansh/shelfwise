package com.shelfwise.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shelfwise.dto.TransferDto;
import com.shelfwise.dto.TransferRequest;
import com.shelfwise.exception.InvalidStatusTransitionException;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.model.BookCopy;
import com.shelfwise.model.CopyStatus;
import com.shelfwise.model.CopyTransfer;
import com.shelfwise.model.LibraryBranch;
import com.shelfwise.model.TransferStatus;
import com.shelfwise.repository.BookCopyRepository;
import com.shelfwise.repository.CopyTransferRepository;
import com.shelfwise.repository.LibraryBranchRepository;
import com.shelfwise.security.SecurityUtils;

/**
 * Moving copies between branches: REQUESTED -> IN_TRANSIT -> RECEIVED
 */
@Service
public class TransferService {

    private final CopyTransferRepository transferRepository;
    private final BookCopyRepository copyRepository;
    private final LibraryBranchRepository branchRepository;

    public TransferService(CopyTransferRepository transferRepository, BookCopyRepository copyRepository,
                           LibraryBranchRepository branchRepository) {
        this.transferRepository = transferRepository;
        this.copyRepository = copyRepository;
        this.branchRepository = branchRepository;
    }

    @Transactional
    public TransferDto requestTransfer(TransferRequest request) {
        BookCopy copy = copyRepository.findById(request.copyId())
                .orElseThrow(() -> new ResourceNotFoundException("Copy not found: " + request.copyId()));
        LibraryBranch toBranch = branchRepository.findById(request.toBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + request.toBranchId()));

        if (copy.getBranch().getId().equals(toBranch.getId())) {
            throw new IllegalArgumentException("Copy is already at branch " + toBranch.getCode());
        }
        // TODO: shouldn't be able to transfer a copy that is checked out

        CopyTransfer transfer = new CopyTransfer();
        transfer.setCopy(copy);
        transfer.setFromBranch(copy.getBranch());
        transfer.setToBranch(toBranch);
        transfer.setStatus(TransferStatus.REQUESTED);
        transfer.setRequestedBy(SecurityUtils.currentUsername());
        transfer.setRequestedAt(LocalDateTime.now());
        return toDto(transferRepository.save(transfer));
    }

    @Transactional
    public TransferDto ship(Long transferId) {
        CopyTransfer transfer = getTransfer(transferId);
        if (transfer.getStatus() != TransferStatus.REQUESTED) {
            throw new InvalidStatusTransitionException(transfer.getStatus().name(), TransferStatus.IN_TRANSIT.name());
        }
        transfer.setStatus(TransferStatus.IN_TRANSIT);
        transfer.setShippedAt(LocalDateTime.now());
        transfer.getCopy().setStatus(CopyStatus.IN_TRANSIT);
        return toDto(transferRepository.save(transfer));
    }

    @Transactional
    public TransferDto receive(Long transferId) {
        CopyTransfer transfer = getTransfer(transferId);
        transfer.setStatus(TransferStatus.RECEIVED);
        transfer.setReceivedAt(LocalDateTime.now());

        BookCopy copy = transfer.getCopy();
        copy.setBranch(transfer.getToBranch());
        copy.setStatus(CopyStatus.AVAILABLE);
        copyRepository.save(copy);

        return toDto(transferRepository.save(transfer));
    }

    @Transactional
    public TransferDto cancel(Long transferId) {
        CopyTransfer transfer = getTransfer(transferId);
        if (transfer.getStatus() == TransferStatus.RECEIVED) {
            throw new InvalidStatusTransitionException(transfer.getStatus().name(), TransferStatus.CANCELLED.name());
        }
        transfer.setStatus(TransferStatus.CANCELLED);
        return toDto(transferRepository.save(transfer));
    }

    public List<TransferDto> getOpenTransfers() {
        return transferRepository.findByStatusIn(List.of(TransferStatus.REQUESTED, TransferStatus.IN_TRANSIT))
                .stream().map(this::toDto).toList();
    }

    public List<TransferDto> getHistoryForCopy(Long copyId) {
        return transferRepository.findByCopyId(copyId).stream().map(this::toDto).toList();
    }

    private CopyTransfer getTransfer(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found: " + id));
    }

    private TransferDto toDto(CopyTransfer t) {
        return new TransferDto(
                t.getId(),
                t.getCopy().getBarcode(),
                t.getCopy().getBook().getTitle(),
                t.getFromBranch() != null ? t.getFromBranch().getCode() : null,
                t.getToBranch().getCode(),
                t.getStatus(),
                t.getRequestedBy(),
                t.getRequestedAt(),
                t.getShippedAt(),
                t.getReceivedAt());
    }
}
