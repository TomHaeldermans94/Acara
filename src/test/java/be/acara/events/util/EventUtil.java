package be.acara.events.util;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.service.mapper.EventMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class EventUtil {
    public static final String RESOURCE_URL = "http://localhost/api/events";
    
    public static Event firstEvent() {
        return Event.builder()
                .id(1L)
                .category(Category.MUSIC)
                .description("event description")
                .eventDate(LocalDateTime.now().plusYears(1L).truncatedTo(ChronoUnit.MINUTES))
                .location("location")
                .name("event name")
                .price(BigDecimal.TEN)
                .image(getImage1AsBytes())
                .build();
    }
    
    public static Event secondEvent() {
        return Event.builder()
                .id(2L)
                .category(Category.THEATRE)
                .description("another event description")
                .eventDate(LocalDateTime.now().plusMonths(6).truncatedTo(ChronoUnit.MINUTES))
                .location("home")
                .name("the name of this event")
                .price(BigDecimal.ONE)
                .image(getImage1AsBytes())
                .build();
    }
    
    public static Event thirdEvent() {
        return Event.builder()
                .id(3L)
                .category(Category.THEATRE)
                .description("another event description")
                .eventDate(LocalDateTime.now().plusMonths(3).truncatedTo(ChronoUnit.MINUTES))
                .location("home")
                .name("the name of this event")
                .price(BigDecimal.ONE)
                .image(getImage1AsBytes())
                .build();
    }
    
    public static EventDto map(Event event) {
        return new EventMapper().map(event);
    }
    
    public static Event map(EventDto event) {
        return new EventMapper().map(event);
    }
    
    public static List<Event> createListsOfEventsOfSize3() {
        return List.of(
                firstEvent(),
                secondEvent(),
                thirdEvent()
        );
    }
    
    public static EventList createEventListOfSize3() {
        List<EventDto> eventDtoList = createListsOfEventsOfSize3().stream().map(EventUtil::map).collect(Collectors.toList());
        return new EventList(eventDtoList);
    }
    
    public static byte[] getImage1AsBytes() {
        try {
            File file = new File("image_event_1.jpg");
            FileInputStream fis = new FileInputStream(file);
            return fis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
