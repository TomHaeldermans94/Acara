package be.acara.events.service.mapper;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.domain.Category;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    public CategoriesList map(Category[] categoryArray){
        List<String> categories = Arrays.stream(Category.values()).map(Category::getWebDisplay).collect(Collectors.toList());
        return new CategoriesList(categories);
    }
}
