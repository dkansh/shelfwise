package com.shelfwise.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shelfwise.dto.AuthorDto;
import com.shelfwise.dto.BookDto;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.mapper.BookMapper;
import com.shelfwise.model.Author;
import com.shelfwise.repository.AuthorRepository;
import com.shelfwise.repository.BookRepository;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository, BookMapper bookMapper) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<AuthorDto> getAuthors(String name) {
        List<Author> authors = name == null
                ? authorRepository.findAll()
                : authorRepository.findByNameContainingIgnoreCase(name);
        return authors.stream().map(this::toDto).toList();
    }

    public AuthorDto getAuthor(Long id) {
        return toDto(findAuthor(id));
    }

    public List<BookDto> getBooksByAuthor(Long authorId) {
        findAuthor(authorId);
        return bookRepository.findByAuthorEntityId(authorId).stream().map(bookMapper::toDto).toList();
    }

    public AuthorDto createAuthor(AuthorDto request) {
        Author author = new Author();
        author.setName(request.name());
        author.setBio(request.bio());
        author.setBirthYear(request.birthYear());
        author.setNationality(request.nationality());
        return toDto(authorRepository.save(author));
    }

    public AuthorDto updateAuthor(Long id, AuthorDto request) {
        Author author = findAuthor(id);
        author.setName(request.name());
        author.setBio(request.bio());
        author.setBirthYear(request.birthYear());
        author.setNationality(request.nationality());
        return toDto(authorRepository.save(author));
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    private Author findAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
    }

    private AuthorDto toDto(Author author) {
        int bookCount = bookRepository.findByAuthorEntityId(author.getId()).size();
        return new AuthorDto(author.getId(), author.getName(), author.getBio(), author.getBirthYear(),
                author.getNationality(), bookCount);
    }
}
