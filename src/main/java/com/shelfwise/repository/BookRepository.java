package com.shelfwise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shelfwise.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByAuthorEntityId(Long authorId);

    List<Book> findByGenreIgnoreCase(String genre);

    @Query("select b from Book b where b.availableCopies > 0")
    List<Book> findAvailableBooks();

    @Query("select b from Book b join b.categories c where c.id = :categoryId")
    List<Book> findByCategoryId(@Param("categoryId") Long categoryId);
}
