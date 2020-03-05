package be.acara.events.service;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.domain.Category;
import be.acara.events.exceptions.EventNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class EventServiceTest {
    
    @Autowired
    private EventService service;
    
    @Test
    void findById() {
        Long idToFind = 1L;
    
        EventDto answer = service.findById(idToFind);
        
        assertThat(answer).isNotNull();
        assertThat(answer.getId()).isEqualTo(1L);
        assertThat(answer.getCategory()).isEqualTo(Category.MUSIC.toString());
        assertThat(answer.getName()).isEqualTo("concert");
        assertThat(answer.getDescription()).isEqualTo("test description");
        assertThat(answer.getImage()).isNull();
        assertThat(answer.getLocation()).isEqualTo("genk");
        assertThat(answer.getPrice()).isEqualTo(new BigDecimal("20").setScale(2, RoundingMode.HALF_EVEN));
    }
    
    @Test
    void findById_notFound() {
        Long idToFind = Long.MAX_VALUE;
        assertThrows(EventNotFoundException.class, () -> service.findById(idToFind));
    }
}
