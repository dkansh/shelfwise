package com.shelfwise.repository.spec;

import org.springframework.data.jpa.domain.Specification;

import com.shelfwise.model.Book;
import com.shelfwise.model.Category;

import jakarta.persistence.criteria.Join;

/**
 * Building blocks for the v2 book search.
 */
public class BookSpecifications {

    public static Specification<Book> titleOrDescriptionContains(String text) {
        String pattern = "%" + text.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern));
    }

    public static Specification<Book> authorContains(String author) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> inCategory(String categorySlug) {
        return (root, query, cb) -> {
            Join<Book, Category> categories = root.join("categories");
            return cb.equal(categories.get("slug"), categorySlug);
        };
    }

    public static Specification<Book> hasLanguage(String language) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("language")), language.toLowerCase());
    }

    public static Specification<Book> availableOnly() {
        return (root, query, cb) -> cb.greaterThan(root.get("availableCopies"), 0);
    }

    public static Specification<Book> publishedAfter(Integer year) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("publishedYear"), year);
    }

    public static Specification<Book> publishedBefore(Integer year) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("publishedYear"), year);
    }
}
