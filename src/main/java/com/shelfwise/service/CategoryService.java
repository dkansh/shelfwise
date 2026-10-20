package com.shelfwise.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.shelfwise.dto.BookDto;
import com.shelfwise.dto.CategoryDto;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.mapper.BookMapper;
import com.shelfwise.model.Book;
import com.shelfwise.model.Category;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.CategoryRepository;
import com.shelfwise.util.StringUtils;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    /**
     * Returns all categories as a tree (top level categories with their children inside).
     */
    @Cacheable("categoryTree")
    public List<CategoryDto> getCategoryTree() {
        List<Category> all = categoryRepository.findAll();
        List<CategoryDto> roots = new ArrayList<>();
        for (Category c : all) {
            if (c.getParent() == null) {
                roots.add(buildTree(c, all));
            }
        }
        return roots;
    }

    private CategoryDto buildTree(Category category, List<Category> all) {
        CategoryDto dto = new CategoryDto(category.getId(), category.getName(), category.getSlug());
        for (Category c : all) {
            if (c.getParent() != null && c.getParent().getId().equals(category.getId())) {
                dto.getChildren().add(buildTree(c, all));
            }
        }
        return dto;
    }

    /**
     * Breadcrumb for a category, e.g. ["Fiction", "Fantasy"].
     */
    public List<String> getPath(Long categoryId) {
        Category current = findCategory(categoryId);
        List<String> path = new ArrayList<>();
        while (current != null) {
            path.add(current.getName());
            current = current.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    @CacheEvict(value = "categoryTree", allEntries = true)
    public CategoryDto createCategory(String name, Long parentId) {
        Category parent = null;
        if (parentId != null) {
            parent = findCategory(parentId);
        }
        Category category = new Category(name, StringUtils.slugify(name), parent);
        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getName(), saved.getSlug());
    }

    public CategoryDto moveCategory(Long categoryId, Long newParentId) {
        Category category = findCategory(categoryId);
        // TODO: stop people from making a category its own grandparent
        category.setParent(newParentId == null ? null : findCategory(newParentId));
        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getName(), saved.getSlug());
    }

    public void assignBook(Long categoryId, Long bookId) {
        Category category = findCategory(categoryId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        book.getCategories().add(category);
        bookRepository.save(book);
    }

    public List<BookDto> getBooksInCategory(Long categoryId) {
        // TODO: include books from sub-categories as well
        return bookRepository.findByCategoryId(categoryId).stream().map(bookMapper::toDto).toList();
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }
}
