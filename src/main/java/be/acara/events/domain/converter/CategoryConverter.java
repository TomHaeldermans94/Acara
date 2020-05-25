package be.acara.events.domain.converter;

import be.acara.events.domain.Category;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converts a Category Enum to a String and vice-versa.
 */
@Converter
public class CategoryConverter implements AttributeConverter<Category, String> {
    /**
     * Converts a Category enum to a string
     *
     * @param category the category enum value to convert to string
     * @return the string value of the provided enum
     */
    @Override
    public String convertToDatabaseColumn(Category category) {
        return category.getWebDisplay();
    }
    
    /**
     * Converts a string value to a Category Enum
     *
     * @param name the category name string
     * @return the Category Enum that matches the provided string
     */
    @Override
    public Category convertToEntityAttribute(String name) {
        return Category.valueOf(name.toUpperCase());
    }
}
