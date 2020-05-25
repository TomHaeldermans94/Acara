package be.acara.events.testutil;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.service.mapper.EventMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
                .amountOfLikes(0)
                .usersThatLikeThisEvent(new HashSet<>())
                .youtubeId("https://www.youtube.com/watch?v=1mdDFyrGkCE")
                .attendees(new HashSet<>())
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
                .youtubeId("https://www.youtube.com/watch?v=1mdDFyrGkCE")
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
                .youtubeId("https://www.youtube.com/watch?v=1mdDFyrGkCE")
                .build();
    }

    public static Event anEventWithTwoAttendees() {
        return Event.builder()
                .id(1L)
                .category(Category.MUSIC)
                .attendees(Set.of(UserUtil.firstUser(), UserUtil.secondUser()))
                .build();
    }

    public static Event anEventWithOneAttendee() {
        return Event.builder()
                .id(2L)
                .category(Category.MUSIC)
                .attendees(Set.of(UserUtil.firstUser()))
                .build();
    }

    public static Event anEventWithZeroAttendees() {
        return Event.builder()
                .id(3L)
                .category(Category.THEATRE)
                .attendees(Set.of())
                .build();
    }

    public static Event anEventWithThreeAttendees() {
        return Event.builder()
                .id(4L)
                .category(Category.THEATRE)
                .attendees(Set.of(UserUtil.firstUser(), UserUtil.secondUser(), UserUtil.thirdUser()))
                .build();
    }

    public static Event anotherEventWithThreeAttendees() {
        return Event.builder()
                .id(5L)
                .category(Category.THEATRE)
                .attendees(Set.of(UserUtil.firstUser(), UserUtil.secondUser(), UserUtil.thirdUser()))
                .build();
    }
    
    public static EventDto map(Event event) {
        return EventMapper.INSTANCE.eventToEventDto(event);
    }
    
    public static Event map(EventDto event) {
        return EventMapper.INSTANCE.eventDtoToEvent(event);
    }
    
    public static List<Event> createListsOfEventsOfSize3() {
        return List.of(
                firstEvent(),
                secondEvent(),
                thirdEvent()
        );
    }
    
    public static Set<Event> createSetOfEventsOfSize3() {
        return new HashSet<>(Set.of(
                firstEvent(),
                secondEvent(),
                thirdEvent()
        ));
    }

    public static List<Event> createListOfEventsOfSize5WithAttendees() {
        return List.of(
                anEventWithOneAttendee(),
                anEventWithThreeAttendees(),
                anotherEventWithThreeAttendees(),
                anEventWithTwoAttendees(),
                anEventWithZeroAttendees()
        );
    }
    
    public static Page<Event> createPageOfEventsOfSize3() {
        return new PageImpl<>(createListsOfEventsOfSize3());
    }

    public static Page<Event> createPageOfEventsOfSize5WithAttendees() {
        return new PageImpl<>(createListOfEventsOfSize5WithAttendees());
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
