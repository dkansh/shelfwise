package com.shelfwise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shelfwise.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByNameContainingIgnoreCase(String name);
}
