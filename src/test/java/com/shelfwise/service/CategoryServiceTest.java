package com.shelfwise.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shelfwise.dto.CategoryDto;
import com.shelfwise.mapper.BookMapper;
import com.shelfwise.model.Category;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void buildsTreeFromFlatList() {
        Category fiction = category(1L, "Fiction", null);
        Category fantasy = category(2L, "Fantasy", fiction);
        Category classics = category(3L, "Classics", fiction);
        Category programming = category(4L, "Programming", null);
        when(categoryRepository.findAll()).thenReturn(List.of(fiction, fantasy, classics, programming));

        List<CategoryDto> tree = categoryService.getCategoryTree();

        assertEquals(2, tree.size());
        assertEquals("Fiction", tree.get(0).getName());
        assertEquals(2, tree.get(0).getChildren().size());
        assertEquals(0, tree.get(1).getChildren().size());
    }

    @Test
    void pathGoesFromRootToCategory() {
        Category fiction = category(1L, "Fiction", null);
        Category fantasy = category(2L, "Fantasy", fiction);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(fantasy));

        assertEquals(List.of("Fiction", "Fantasy"), categoryService.getPath(2L));
    }

    private Category category(Long id, String name, Category parent) {
        Category c = new Category(name, name.toLowerCase(), parent);
        c.setId(id);
        return c;
    }
}
