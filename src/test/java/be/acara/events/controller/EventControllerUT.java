package be.acara.events.controller;


import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.service.EventService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class EventControllerUT {

    private EventController controller;
    private EventService service = Mockito.mock(EventService.class);

    @BeforeEach
    void setUp() {
        controller = new EventController(service);
    }

    @Test
    void findById() {
        long idToFind = 1L;
        Mockito.when(service.findById(idToFind)).thenReturn(createEventDto());
        Assertions.assertThat(controller.findById(idToFind)).isEqualTo(new ResponseEntity<>(createEventDto(), HttpStatus.OK));
    }

    @Test
    void findById_notFound(){
        long idToFind = Long.MAX_VALUE;
        Mockito.when(service.findById(idToFind)).thenThrow(EventNotFoundException.class);
        assertThrows(EventNotFoundException.class, () -> controller.findById(idToFind));
    }

    @Test
    void findAll() throws Exception {
        Mockito.when(service.findAllByAscendingDate()).thenReturn(createEventDtoListForDateTesting());
        Assertions.assertThat(controller.findAllByAscendingDate()).isEqualTo(new ResponseEntity<>(createEventDtoListForDateTesting(), HttpStatus.OK));
    }

    @Test
    void deleteById(){
        long idToDelete = 1L;
        Mockito.doNothing().when(service).deleteEvent(idToDelete);
        Assertions.assertThat(controller.deleteEvent(idToDelete)).isEqualTo(new ResponseEntity<>("Event deleted succesfully",HttpStatus.NO_CONTENT));
    }

    private EventDto createEventDto() {
        return EventDto.builder()
                .id(1L)
                .name("concert")
                .location("genk")
                .category("Music")
                .eventDate(LocalDateTime.of(2020,12,20,20,30,54))
                .description("description")
                .price(new BigDecimal("20.00"))
                .build();
    }

    private EventDto createEventDto2() {
        return EventDto.builder()
                .id(2L)
                .name("concert")
                .location("genk")
                .category("Music")
                .eventDate(LocalDateTime.of(2020,11,21,21,31,55))
                .description("description")
                .price(new BigDecimal("20.1"))
                .build();
    }

    private EventList createEventDtoListForDateTesting() throws Exception {
        List<EventDto> eventDtos = new ArrayList<>();
        eventDtos.add(createEventDto2());
        eventDtos.add(createEventDto());
        return new EventList(eventDtos);
    }
}
