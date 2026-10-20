package com.shelfwise.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.TransferDto;
import com.shelfwise.dto.TransferRequest;
import com.shelfwise.service.TransferService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transfers")
@PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferDto> request(@Valid @RequestBody TransferRequest request) {
        return new ResponseEntity<>(transferService.requestTransfer(request), HttpStatus.CREATED);
    }

    @GetMapping("/open")
    public List<TransferDto> openTransfers() {
        return transferService.getOpenTransfers();
    }

    @GetMapping("/copy/{copyId}")
    public List<TransferDto> history(@PathVariable Long copyId) {
        return transferService.getHistoryForCopy(copyId);
    }

    @PostMapping("/{id}/ship")
    public TransferDto ship(@PathVariable Long id) {
        return transferService.ship(id);
    }

    @PostMapping("/{id}/receive")
    public TransferDto receive(@PathVariable Long id) {
        return transferService.receive(id);
    }

    @PostMapping("/{id}/cancel")
    public TransferDto cancel(@PathVariable Long id) {
        return transferService.cancel(id);
    }
}
