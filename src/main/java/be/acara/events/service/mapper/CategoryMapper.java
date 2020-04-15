package be.acara.events.service.mapper;

import be.acara.events.controller.dto.CategoryDto;
import be.acara.events.domain.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    public CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
    
    default CategoryDto categoryToCategoryDto(Category category) {
        return new CategoryDto(category.name(), category.getWebDisplay());
    }
    
    default Category categoryDtoToCategory(CategoryDto category) {
        return Category.valueOf(category.getName());
    }
}