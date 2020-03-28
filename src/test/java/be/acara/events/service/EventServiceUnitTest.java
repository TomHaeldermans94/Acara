package be.acara.events.service;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.service.mapper.CategoryMapper;
import be.acara.events.service.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceUnitTest {
    
    @Mock
    private EventRepository eventRepository;
    private EventService service;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        EventMapper eventMapper = new EventMapper();
        CategoryMapper categoryMapper = new CategoryMapper();
        service = new EventService(eventRepository, eventMapper,categoryMapper);
    }

    @Test
    void findById() throws Exception {
        Long idToFind = 1L;

        Mockito.when(eventRepository.findById(idToFind)).thenReturn(Optional.of(createEvent()));
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

        Mockito.when(eventRepository.findAllByOrderByEventDateAsc()).thenReturn(createEventListForDateTesting());
        EventList answer = service.findAllByAscendingDate();
        assertThat(answer).isNotNull();
        assertThat(answer.getEventList().size()).isEqualTo(2);
        assertThat(answer.getEventList()).extracting(EventDto::getEventDate).containsExactly(createEvent2().getEventDate(), createEvent().getEventDate());
    }

    @Test
    void deleteEvent(){
        Long id = 1L;
        Mockito.when(eventRepository.existsById(id)).thenReturn(true);
        Mockito.doNothing().when(eventRepository).deleteById(id);
        service.deleteEvent(id);
        verify(eventRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_notFound() {
        Long idToDelete = Long.MAX_VALUE;
        Mockito.when(eventRepository.existsById(idToDelete)).thenThrow(EventNotFoundException.class);
        assertThrows(EventNotFoundException.class, () -> service.deleteEvent(idToDelete));
    }
    
    @Test
    void findById_notFound() {
        Long idToFind = Long.MAX_VALUE;
        Mockito.when(eventRepository.findById(idToFind)).thenThrow(EventNotFoundException.class);
        assertThrows(EventNotFoundException.class, () -> service.findById(idToFind));
    }
    
    @Test
    void search_emptyParams() {
        Map<String, String> params = new HashMap<>();
        EventList search = service.search(params);
        
        assertThat(search).isNotNull();
        assertThat(search.getEventList()).isNotNull();
        assertThat(search.getEventList()).isEmpty();
    }
    
    @Test
    void search_withParams() throws Exception {
        Map<String, String> params = new HashMap<>();
        Event event = createEvent();
        params.put("location",event.getLocation());
        params.put("minPrice",event.getPrice().toString());
        params.put("maxPrice",event.getPrice().toString());
        params.put("startDate",event.getEventDate().toString());
        params.put("endDate",event.getEventDate().toString());
        when(eventRepository.findAll(any(Specification.class))).thenReturn(List.of(event));
        EventList search = service.search(params);
        
        assertThat(search).isNotNull();
        assertThat(search.getEventList()).isNotNull();
        assertThat(search.getEventList()).isNotEmpty();
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
