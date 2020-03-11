package be.acara.events.service;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.exceptions.EventNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class EventServiceTest {
    
    @Autowired
    private EventService service;
    
    @Test
    void findById() throws IOException, SQLException {
        Long idToFind = 1L;
    
        EventDto answer = service.findById(idToFind);
        
        assertThat(answer).isNotNull();
        assertThat(answer.getId()).isEqualTo(1L);
        assertThat(answer.getCategory()).isEqualTo(Category.MUSIC.toString());
        assertThat(answer.getName()).isEqualTo("concert");
        assertThat(answer.getDescription()).isEqualTo("test description");
        assertThat(answer.getImage()).isEqualTo(getImageBytes("image_event_1.jpg"));
        assertThat(answer.getLocation()).isEqualTo("genk");
        assertThat(answer.getPrice()).isEqualTo(new BigDecimal("20").setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void findAllByAscendingDate() {
        EventList answer = service.findAllByAscendingDate();
        assertThat(answer).isNotNull();
        assertThat(answer.getEventList().size() == 2);
    }

    @Test
    void deleteEvent() {
        EventList events = service.findAllByAscendingDate();
        assertThat(events).isNotNull();
        assertThat(events.getEventList().size() == 2);
        service.deleteEvent(1);
        assertThat(events.getEventList().size() == 1);
    }
    
    @Test
    void findById_notFound() {
        Long idToFind = Long.MAX_VALUE;
        assertThrows(EventNotFoundException.class, () -> service.findById(idToFind));
    }
    
    private byte[] getImageBytes(String imageLocation) throws IOException, SQLException {
        File file = new File(imageLocation);
        FileInputStream fis = new FileInputStream(file);
        return fis.readAllBytes();
    }
}
