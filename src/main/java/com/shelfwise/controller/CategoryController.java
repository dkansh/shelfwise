package com.shelfwise.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shelfwise.dto.BookDto;
import com.shelfwise.dto.CategoryDto;
import com.shelfwise.service.CategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public List<CategoryDto> getTree() {
        return categoryService.getCategoryTree();
    }

    @GetMapping("/{id}/path")
    public List<String> getPath(@PathVariable Long id) {
        return categoryService.getPath(id);
    }

    @GetMapping("/{id}/books")
    public List<BookDto> getBooks(@PathVariable Long id) {
        return categoryService.getBooksInCategory(id);
    }

    @PostMapping
    public CategoryDto create(@RequestParam String name, @RequestParam(required = false) Long parentId) {
        return categoryService.createCategory(name, parentId);
    }

    @PutMapping("/{id}/parent")
    public CategoryDto move(@PathVariable Long id, @RequestParam(required = false) Long parentId) {
        return categoryService.moveCategory(id, parentId);
    }

    @PostMapping("/{categoryId}/books/{bookId}")
    public ResponseEntity<Void> assignBook(@PathVariable Long categoryId, @PathVariable Long bookId) {
        categoryService.assignBook(categoryId, bookId);
        return ResponseEntity.ok().build();
    }
}
