package com.shelfwise.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.BookCopyDto;
import com.shelfwise.dto.CreateCopyRequest;
import com.shelfwise.model.CopyCondition;
import com.shelfwise.service.BookCopyService;

@RestController
@RequestMapping("/api/v1/copies")
public class BookCopyController {

    private final BookCopyService copyService;

    public BookCopyController(BookCopyService copyService) {
        this.copyService = copyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<BookCopyDto> addCopy(@RequestBody CreateCopyRequest request) {
        return new ResponseEntity<>(copyService.addCopy(request), HttpStatus.CREATED);
    }

    @GetMapping("/book/{bookId}")
    public List<BookCopyDto> getForBook(@PathVariable Long bookId) {
        return copyService.getCopiesForBook(bookId);
    }

    @GetMapping("/branch/{branchId}")
    public List<BookCopyDto> getForBranch(@PathVariable Long branchId) {
        return copyService.getCopiesAtBranch(branchId);
    }

    @GetMapping("/barcode/{barcode}")
    public BookCopyDto getByBarcode(@PathVariable String barcode) {
        return copyService.findByBarcode(barcode);
    }

    @PatchMapping("/{id}/condition")
    public BookCopyDto updateCondition(@PathVariable Long id, @RequestParam CopyCondition condition) {
        return copyService.updateCondition(id, condition);
    }

    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> withdraw(@PathVariable Long id) {
        copyService.withdrawCopy(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/lost")
    public ResponseEntity<Void> markLost(@PathVariable Long id) {
        copyService.markLost(id);
        return ResponseEntity.ok().build();
    }
}
