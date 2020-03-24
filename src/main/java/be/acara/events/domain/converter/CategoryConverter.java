package be.acara.events.domain.converter;

import be.acara.events.domain.Category;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CategoryConverter implements AttributeConverter<Category, String> {
    @Override
    public String convertToDatabaseColumn(Category category) {
        return category.getWebDisplay();
    }

    @Override
    public Category convertToEntityAttribute(String name) {
        return Category.valueOf(name.toUpperCase());
    }
}
