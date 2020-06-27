package be.acara.events.service;

import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.exceptions.*;
import be.acara.events.repository.EventRepository;
import be.acara.events.testutil.EventUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static be.acara.events.testutil.EventUtil.*;
import static be.acara.events.testutil.UserUtil.firstUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Unit Test")
class EventServiceUnitTest {
    private final static PageRequest PAGE_REQUEST = PageRequest.of(0, 25, Sort.by("eventDate").ascending());
    private final static Long ID = 1L;
    
    /*******************************
     *         dependencies        *
     *******************************/
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserService userService;
    
    /*******************************
     *         Class to test       *
     *******************************/
    private EventService eventService;
    
    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventRepository, userService);
    }
    
    @Nested
    @DisplayName("Find events")
    class FindEvents {
        
        @Test
        @DisplayName("Find by id")
        void findById() {
            Event event = firstEvent();
            when(eventRepository.findById(ID)).thenReturn(Optional.of(event));
            Event answer = eventService.findById(ID);
            
            assertEvent(answer, event);
            verifyOnce().findById(ID);
        }
        
        @Test
        @DisplayName("Find all (by ascending date)")
        void findAllByAscendingDate() {
            when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(createPageOfEventsOfSize3());
            Page<Event> answer = eventService.findAll(Collections.emptyMap(), PAGE_REQUEST);
            
            assertPage(answer, createPageOfEventsOfSize3());
            verifyOnce().findAll(any(Specification.class), any(Pageable.class));
        }
        
        @Test
        @DisplayName("Find by id - not found")
        void findById_notFound() {
            Long idToFind = Long.MAX_VALUE;
            when(eventRepository.findById(idToFind)).thenReturn(Optional.empty());
            
            EventNotFoundException thrownException = assertThrows(EventNotFoundException.class, () -> eventService.findById(idToFind));
            
            assertThat(thrownException).isNotNull();
            assertThat(thrownException.getTitle()).isEqualTo("Event not found");
            assertThat(thrownException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(thrownException.getMessage()).isEqualTo(String.format("Event with ID %d not found", idToFind));
            
            verifyOnce().findById(idToFind);
        }
        
        @Test
        @DisplayName("Find all events from user by provided user id")
        void findEventsByUserId() {
            User user = firstUser();
            when(userService.findById(any())).thenReturn(user);
            when(eventRepository.findAllByAttendeesContains(any(), any())).thenReturn(createPageOfEventsOfSize3());
            Page<Event> answer = eventService.findEventsByUserId(ID, PAGE_REQUEST);
            
            assertPage(answer, createPageOfEventsOfSize3());
            verifyOnce().findAllByAttendeesContains(any(), eq(PAGE_REQUEST));
            verifyUserServiceOnce().findById(any());
        }
        
        @Test
        @DisplayName("Find liked events from user by provided user id")
        void findLikedEventsByUserId() {
            User user = firstUser();
            when(userService.findById(any())).thenReturn(user);
            when(eventRepository.findAllByUsersThatLikeThisEventContains(any(), any())).thenReturn(createPageOfEventsOfSize3());
            Page<Event> answer = eventService.findLikedEventsByUserId(ID, PAGE_REQUEST);
            
            assertPage(answer, createPageOfEventsOfSize3());
            verifyOnce().findAllByUsersThatLikeThisEventContains(any(), eq(PAGE_REQUEST));
        }
        
        @Test
        @DisplayName("Find the 4 most popular events")
        void findMostPopularEvents() {
            List<Event> expectedEvents = List.of(EventUtil.anEventWithThreeAttendees(), EventUtil.anotherEventWithThreeAttendees(), EventUtil.anEventWithTwoAttendees(), EventUtil.anEventWithOneAttendee());
            
            when(eventRepository.findTop4ByAttendeesSize(any(Pageable.class))).thenReturn(expectedEvents);
            
            
            List<Event> events = eventService.mostPopularEvents();
            
            assertThat(events.size()).isEqualTo(4);
            assertThat(events).isEqualTo(expectedEvents);
        }
        
        @Test
        @DisplayName("Find related events, given an events")
        void findRelatedEvents() {
            List<Event> expectedEvents = List.of(EventUtil.anEventWithOneAttendee(), EventUtil.anEventWithTwoAttendees());
            when(eventRepository.getRelatedEvents(any(), any(), any())).thenReturn(expectedEvents);
            
            List<Event> events = eventService.relatedEvents(firstEvent());
            
            assertThat(events.size()).isEqualTo(2);
            assertThat(events).isEqualTo(expectedEvents);
        }
        
        @Test
        @DisplayName("Find all with search params")
        void search_withParams() {
            Map<String, String> params = new HashMap<>();
            Event event = firstEvent();
            PageImpl<Event> events = new PageImpl<>(List.of(event));
            params.put("location", event.getLocation());
            params.put("minPrice", event.getPrice().toString());
            params.put("maxPrice", event.getPrice().toString());
            params.put("startDate", event.getEventDate().toString());
            params.put("endDate", event.getEventDate().toString());
            when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(events);
            Page<Event> search = eventService.findAll(params, PageRequest.of(0, 20));
            
            assertPage(search, events);
            verifyOnce().findAll(any(Specification.class), any(PageRequest.class));
        }
    }
    
    private UserService verifyUserServiceOnce() {
        return verify(userService, times(1));
    }
    
    @Nested
    @DisplayName("Add events")
    class AddEvents {
        
        @Test
        @DisplayName("Add event")
        void addEvent() {
            Event event = firstEvent();
            event.setId(null);
            
            when(eventRepository.saveAndFlush(event)).thenReturn(event);
            Event answer = eventService.addEvent(event);
            
            assertEvent(answer, event);
            verifyOnce().saveAndFlush(event);
        }
        
        @Test
        @DisplayName("Add event with invalid date")
        void addEvent_withInvalidDate() {
            Event event = firstEvent();
            event.setId(null);
            event.setEventDate(LocalDateTime.now().minusDays(10));
            assertThrows(InvalidDateException.class, () -> eventService.addEvent(event));
        }
        
        @Test
        @DisplayName("Add event with invalid YouTube-URL")
        void addEvent_withInvalidYoutubeUrl() {
            Event event = firstEvent();
            event.setId(null);
            event.setYoutubeId("https://www.youtube.com/watch?v=1mdDFyrGkCE");
            InvalidYoutubeUrlException invalidYoutubeUrlException = assertThrows(InvalidYoutubeUrlException.class, () -> eventService.addEvent(event));
        }
        
        @Test
        @DisplayName("Add event with null YouTube-URL")
        void addEvent_withNullYoutubeUrl() {
            InvalidYoutubeUrlException invalidYoutubeUrlException = assertThrows(InvalidYoutubeUrlException.class, () -> eventService.addEvent(eventWithNullYoutubeId()));
            
            assertThat(invalidYoutubeUrlException.getTitle()).isEqualTo("Invalid youtube URL");
        }
        
        @Test
        @DisplayName("Add event with empty/blank YouTube-URL")
        void addEvent_withBlankYoutubeUrl() {
            Event event = firstEvent();
            event.setId(null);
            event.setYoutubeId("");
            
            when(eventRepository.saveAndFlush(event)).thenReturn(event);
            Event answer = eventService.addEvent(event);
            
            assertEvent(answer, event);
            verifyOnce().saveAndFlush(event);
        }
        
        @Test
        @DisplayName("Add event with existing id")
        void addEvent_withExistingId() {
            Event event = firstEvent();
            
            IdAlreadyExistsException idAlreadyExistsException = assertThrows(IdAlreadyExistsException.class, () -> eventService.addEvent(event));
            
            assertThat(idAlreadyExistsException).isNotNull();
            assertThat(idAlreadyExistsException.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
            assertThat(idAlreadyExistsException.getMessage()).isEqualTo("A new entity cannot already contain an id");
            assertThat(idAlreadyExistsException.getTitle()).isEqualTo("Cannot process entry");
            verifyNoInteractions(eventRepository);
        }
    }
    
    @Nested
    @DisplayName("Delete events")
    class DeleteEvents {
        @Test
        @DisplayName("Delete event")
        void deleteEvent() {
            Event eventToDelete = firstEvent();
            when(eventRepository.existsById(1L)).thenReturn(true);
            eventService.deleteEvent(firstEvent().getId());
            verifyOnce().deleteById(eventToDelete.getId());
        }
        
        @Test
        @DisplayName("Delete event - not found")
        void deleteEvent_notFound() {
            when(eventRepository.existsById(anyLong())).thenReturn(false);
            EventNotFoundException eventNotFoundException = assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(firstEvent().getId()));
            
            assertThat(eventNotFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(eventNotFoundException.getTitle()).isEqualTo("Event not found");
            assertThat(eventNotFoundException.getMessage()).isEqualTo(String.format("Event with ID %d not found", ID));
            
            verifyOnce().existsById(ID);
        }
    }
    
    @Nested
    @DisplayName("Categories")
    class Categories {
        @Test
        @DisplayName("Get all Categories")
        void getAllCategories() {
            List<Category> answer = eventService.getAllCategories();
            
            assertThat(answer).isNotNull();
            assertThat(answer).isNotNull();
            assertThat(answer.size()).isEqualTo(Category.values().length);
            
            List<Category> listOfCategoryValues = Arrays.stream(Category.values()).collect(Collectors.toList());
            assertThat(answer).isEqualTo(listOfCategoryValues);
            verifyNoInteractions(eventRepository);
        }
    }
    
    @Nested
    @DisplayName("Edit events")
    class EditEvents {
        @Test
        @DisplayName("Edit event")
        void editEvent() {
            Event firstEvent = firstEvent();
            Event secondEvent = secondEvent();
            secondEvent.setId(firstEvent.getId());
            
            when(eventRepository.saveAndFlush(secondEvent)).thenReturn(secondEvent);
            Event answer = eventService.editEvent(secondEvent.getId(), secondEvent);
            
            assertEvent(answer, secondEvent);
            verifyOnce().saveAndFlush(secondEvent);
        }
        
        @Test
        @DisplayName("Edit event with mismatching id")
        void editEvent_withMismatchingId() {
            Event firstEvent = firstEvent();
            Event secondEvent = secondEvent();
            
            IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, () -> eventService.editEvent(firstEvent.getId(), secondEvent));
            
            assertThat(idNotFoundException).isNotNull();
            assertThat(idNotFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(idNotFoundException.getTitle()).isEqualTo("Cannot process entry");
            assertThat(idNotFoundException.getMessage()).isEqualTo(String.format("Id of event to edit does not match given id. Event id = %d, and given id = %d", secondEvent.getId(), firstEvent.getId()));
            
            verifyNoInteractions(eventRepository);
        }
    }
    
    private EventRepository verifyOnce() {
        return verify(eventRepository, times(1));
    }
    
    
    private void assertEvent(Event answer, Event givenEvent) {
        assertThat(answer).isEqualTo(givenEvent);
        assertThat(answer.getId()).isEqualTo(givenEvent.getId());
        assertThat(answer.getEventDate()).isEqualTo(givenEvent.getEventDate());
        assertThat(answer.getPrice()).isEqualTo(givenEvent.getPrice());
        assertThat(answer.getImage()).isEqualTo(givenEvent.getImage());
        assertThat(answer.getLocation()).isEqualTo(givenEvent.getLocation());
        assertThat(answer.getCategory()).isEqualTo(givenEvent.getCategory());
        assertThat(answer.getDescription()).isEqualTo(givenEvent.getDescription());
        assertThat(answer.getName()).isEqualTo(givenEvent.getName());
    }
    
    private void assertPage(Page<Event> eventList, Page<Event> givenList) {
        assertThat(eventList).isNotNull();
        assertThat(eventList.getContent().size()).isGreaterThanOrEqualTo(0);
        IntStream.range(0, eventList.getSize())
                .forEach(value -> {
                    assertEvent(eventList.getContent().get(value), givenList.getContent().get(value));
                });
    }
}
