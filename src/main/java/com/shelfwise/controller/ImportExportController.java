package com.shelfwise.controller;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shelfwise.dto.ImportResult;
import com.shelfwise.service.BookImportService;
import com.shelfwise.service.ExportService;

@RestController
@RequestMapping("/api/v1/data")
public class ImportExportController {

    private final BookImportService importService;
    private final ExportService exportService;

    public ImportExportController(BookImportService importService, ExportService exportService) {
        this.importService = importService;
        this.exportService = exportService;
    }

    @PostMapping("/import/books")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ImportResult importBooks(@RequestParam("file") MultipartFile file) throws IOException {
        return importService.importBooks(file);
    }

    @GetMapping("/export/books.csv")
    public ResponseEntity<String> exportBooks() {
        return csv("books.csv", exportService.exportBooksCsv());
    }

    @GetMapping("/export/overdue-loans.csv")
    public ResponseEntity<String> exportOverdueLoans() {
        return csv("overdue-loans.csv", exportService.exportOverdueLoansCsv());
    }

    private ResponseEntity<String> csv(String filename, String body) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }
}
