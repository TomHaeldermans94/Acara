package be.acara.events.service.mapper;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class EventMapperUnitTest {
    private EventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EventMapper();
    }

    @Test
    void mapFromEventToEventDto() throws Exception {
        Assertions.assertThat(createEventDto()).isEqualTo(mapper.map(createEvent()));
    }

    @Test
    void mapFromEventDtoToEvent() throws Exception {
        Assertions.assertThat(createEvent()).isEqualTo(mapper.map(createEventDto()));
    }

    @Test
    void mapFromEventListToListOfEvents() throws Exception {
        Assertions.assertThat(createListOfEvents()).isEqualTo(mapper.mapDtoListToEntityList(createListOfEventDtos()));
    }

    @Test
    void mapFromListOfEventsToEventList() throws Exception {
        Assertions.assertThat(createListOfEventDtos()).isEqualTo(mapper.mapEntityListToDtoList(createListOfEvents()));
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
                .build();
    }

    private EventDto createEventDto() throws Exception {
        return EventDto.builder()
                .id(1L)
                .name("concert")
                .location("genk")
                .category("MUSIC")
                .eventDate(LocalDateTime.of(2020,12,20,20,30,54))
                .description("description")
                .price(new BigDecimal("20.00"))
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
                .build();
    }

    private EventDto createEventDto2() throws Exception {
        return EventDto.builder()
                .id(2L)
                .name("concert")
                .location("genk")
                .category("MUSIC")
                .eventDate(LocalDateTime.of(2020,11,21,21,31,55))
                .description("description")
                .price(new BigDecimal("20.1"))
                .build();
    }

    private List<Event> createListOfEvents() throws Exception {
        List<Event> events = new ArrayList<>();
        events.add(createEvent2());
        events.add(createEvent());
        return events;
    }

    private List<EventDto> createListOfEventDtos() throws Exception {
        List<EventDto> events = new ArrayList<>();
        events.add(createEventDto2());
        events.add(createEventDto());
        return events;
    }
}
