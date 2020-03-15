package be.acara.events.service;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class EventServiceUT {
    
    @Autowired
    private EventService service;

    @MockBean
    private EventRepository repository;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findById() throws Exception {
        Long idToFind = 1L;

        Mockito.when(repository.findById(idToFind)).thenReturn(Optional.of(createEvent()));
        EventDto answer = service.findById(idToFind);
        
        assertThat(answer).isNotNull();
        assertThat(answer.getId()).isEqualTo(1L);
        assertThat(answer.getCategory()).isEqualTo(Category.MUSIC.toString());
        assertThat(answer.getName()).isEqualTo("concert");
        assertThat(answer.getDescription()).isEqualTo("description");
        assertThat(answer.getImage()).isEqualTo(getImageBytes("image_event_1.jpg"));
        assertThat(answer.getLocation()).isEqualTo("genk");
        assertThat(answer.getPrice()).isEqualTo(new BigDecimal("20.00").setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void findAllByAscendingDate() throws Exception {

        Mockito.when(repository.findAllByOrderByEventDateAsc()).thenReturn(createEventListForDateTesting());
        EventList answer = service.findAllByAscendingDate();
        assertThat(answer).isNotNull();
        assertThat(answer.getEventList().size()).isEqualTo(2);
        assertThat(answer.getEventList()).extracting(EventDto::getEventDate).containsExactly(createEvent2().getEventDate(), createEvent().getEventDate());
    }

    @Test
    void deleteEvent() throws Exception {
        Event eventToDelete = createEvent();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(eventToDelete));
        service.deleteEvent(1L);
        verify(repository, times(1)).delete(eventToDelete);
    }
    
    @Test
    void findById_notFound() {
        Long idToFind = Long.MAX_VALUE;
        Mockito.when(repository.findById(idToFind)).thenThrow(EventNotFoundException.class);
        assertThrows(EventNotFoundException.class, () -> service.findById(idToFind));
    }
    
    private byte[] getImageBytes(String imageLocation) throws IOException, SQLException {
        File file = new File(imageLocation);
        FileInputStream fis = new FileInputStream(file);
        return fis.readAllBytes();
    }

    private Event createEvent() throws Exception {
        return Event.builder()
                .id(1L)
                .name("concert")
                .location("genk")
                .category(Category.MUSIC)
                .eventDate(LocalDateTime.of(2020,12,20,20,30,54))
                .description("description")
                .price(new BigDecimal("20.00"))
                .image(getImageBytes("image_event_1.jpg"))
                .build();
    }

    private Event createEvent2() throws Exception {
        return Event.builder()
                .id(2L)
                .name("concert")
                .location("genk")
                .category(Category.MUSIC)
                .eventDate(LocalDateTime.of(2020,11,21,21,31,55))
                .description("description")
                .price(new BigDecimal("20.1"))
                .image(getImageBytes("image_event_1.jpg"))
                .build();
    }

    private List<Event> createEventListForDateTesting() throws Exception {
        List<Event> events = new ArrayList<>();
        events.add(createEvent2());
        events.add(createEvent());
        return events;
    }
}
