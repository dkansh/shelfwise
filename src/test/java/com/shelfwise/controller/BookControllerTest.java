package com.shelfwise.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.shelfwise.dto.BookDto;
import com.shelfwise.exception.ResourceNotFoundException;
import com.shelfwise.service.BookService;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false) // skip spring security for these tests
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void getBook_returnsBook() throws Exception {
        when(bookService.getBook(1L)).thenReturn(
                new BookDto(1L, "9780132350884", "Clean Code", "Robert C. Martin", 2008, "Programming", 3, 2));

        mockMvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void getBook_returns404WhenMissing() throws Exception {
        when(bookService.getBook(99L)).thenThrow(new ResourceNotFoundException("Book not found with id 99"));

        mockMvc.perform(get("/api/v1/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found with id 99"));
    }

    @Test
    void createBook_returns400WhenTitleMissing() throws Exception {
        String json = """
                {
                  "isbn": "9780132350884",
                  "author": "Robert C. Martin",
                  "totalCopies": 1
                }
                """;

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("title is required"));
    }
}
