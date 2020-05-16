package be.acara.events.service.mapper;

import be.acara.events.controller.dto.CategoryDto;
import be.acara.events.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {
    
    private CategoryMapper categoryMapper;
    
    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapperImpl();
    }
    
    @Test
    void categoryToCategoryDto() {
        Category category = Category.MUSIC;
        CategoryDto categoryDto = new CategoryDto(category.name(), category.getWebDisplay());
    
        CategoryDto answer = categoryMapper.categoryToCategoryDto(category);
        
        assertThat(answer).isEqualTo(categoryDto);
    }
}
