package be.acara.events.service;

import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.exceptions.*;
import be.acara.events.repository.EventRepository;
import be.acara.events.testutil.EventUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
class EventServiceUnitTest {
    private final static PageRequest PAGE_REQUEST = PageRequest.of(0,25, Sort.by("eventDate").ascending());

    @Mock
    private EventRepository eventRepository;
    private EventService eventService;
    @Mock
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventRepository, userService);
    }
    
    @Test
    void findById() {
        Long idToFind = 1L;
    
        Event event = firstEvent();
        when(eventRepository.findById(idToFind)).thenReturn(Optional.of(event));
        Event answer = eventService.findById(idToFind);
        
        assertEvent(answer, event);
        verify(eventRepository, times(1)).findById(idToFind);
    }
    
    @Test
    void findAllByAscendingDate() {
        when(eventRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(createPageOfEventsOfSize3());
        Page<Event> answer = eventService.findAll(Collections.emptyMap(), PAGE_REQUEST);
    
        assertPage(answer, createPageOfEventsOfSize3());
        verify(eventRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findMostPopularEvents() {
        Mockito.when(eventRepository.findAll()).thenReturn(createListOfEventsOfSize5WithAttendees());
        List<Event> expectedEvents = List.of(EventUtil.anEventWithThreeAttendees(), EventUtil.anotherEventWithThreeAttendees(),  EventUtil.anEventWithTwoAttendees(), EventUtil.anEventWithOneAttendee());

        List<Event> events = eventService.mostPopularEvents();

        assertThat(events.size()).isEqualTo(4);
        assertThat(events).isEqualTo(expectedEvents);
    }

    @Test
    void findRelatedEvents() {
        Mockito.when(eventRepository.findAll()).thenReturn(createListOfEventsOfSize5WithAttendees());
        List<Event> expectedEvents = List.of(EventUtil.anEventWithOneAttendee(), EventUtil.anEventWithTwoAttendees());

        List<Event> events = eventService.relatedEvents(firstEvent());

        assertThat(events.size()).isEqualTo(2);
        assertThat(events).isEqualTo(expectedEvents);
    }
    
    @Test
    void deleteEvent() {
        Event eventToDelete = firstEvent();
        when(eventRepository.existsById(1L)).thenReturn(true);
        eventService.deleteEvent(firstEvent().getId());
        verify(eventRepository, times(1)).deleteById(eventToDelete.getId());
    }
    
    @Test
    void deleteEvent_notFound() {
        Long id = 1L;
        when(eventRepository.existsById(anyLong())).thenReturn(false);
        EventNotFoundException eventNotFoundException = assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(firstEvent().getId()));
        
        assertThat(eventNotFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(eventNotFoundException.getTitle()).isEqualTo("Event not found");
        assertThat(eventNotFoundException.getMessage()).isEqualTo(String.format("Event with ID %d not found", id));
        
        verify(eventRepository, times(1)).existsById(id);
    }
    
    @Test
    void findById_notFound() {
        Long idToFind = Long.MAX_VALUE;
        when(eventRepository.findById(idToFind)).thenReturn(Optional.empty());
        
        EventNotFoundException thrownException = assertThrows(EventNotFoundException.class, () -> eventService.findById(idToFind));
        
        assertThat(thrownException).isNotNull();
        assertThat(thrownException.getTitle()).isEqualTo("Event not found");
        assertThat(thrownException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(thrownException.getMessage()).isEqualTo(String.format("Event with ID %d not found", idToFind));
        
        verify(eventRepository, times(1)).findById(idToFind);
    }
    
    @Test
    void search_emptyParams() {
        Map<String, String> params = new HashMap<>();
        when(eventRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        Page<Event> search = eventService.findAll(params, PageRequest.of(0, 20));
    
        assertThat(search).isNotNull();
        assertThat(search.getContent()).isNotNull();
        assertThat(search.getContent()).isEmpty();
        
        verify(eventRepository, times(0)).findAll();
    }
    
    @Test
    void search_withParams() {
        Map<String, String> params = new HashMap<>();
        Event event = firstEvent();
        PageImpl<Event> events = new PageImpl<>(List.of(event));
        params.put("location",event.getLocation());
        params.put("minPrice",event.getPrice().toString());
        params.put("maxPrice",event.getPrice().toString());
        params.put("startDate",event.getEventDate().toString());
        params.put("endDate",event.getEventDate().toString());
        when(eventRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(events);
        Page<Event> search = eventService.findAll(params, PageRequest.of(0,20));

        assertPage(search, events);
        verify(eventRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
    }
    
    @Test
    void getAllCategories() {
        List<Category> answer = eventService.getAllCategories();
        
        assertThat(answer).isNotNull();
        assertThat(answer).isNotNull();
        assertThat(answer.size()).isEqualTo(Category.values().length);
        
        List<Category> listOfCategoryValues = Arrays.stream(Category.values()).collect(Collectors.toList());
        assertThat(answer).isEqualTo(listOfCategoryValues);
        verifyNoInteractions(eventRepository);
    }
    
    @Test
    void addEvent() {
        Event event = firstEvent();
        event.setId(null);
        
        when(eventRepository.saveAndFlush(event)).thenReturn(event);
        Event answer = eventService.addEvent(event);
        
        assertEvent(answer, event);
        verify(eventRepository, times(1)).saveAndFlush(event);
    }
    
    @Test
    void addEvent_withInvalidDate() {
        Event event = firstEvent();
        event.setId(null);
        event.setEventDate(LocalDateTime.now().minusDays(10));
        assertThrows(InvalidDateException.class, () -> eventService.addEvent(event));
    }

    @Test
    void addEvent_withInvalidYoutubeUrl() {
        Event event = firstEvent();
        event.setId(null);
        event.setYoutubeId("1mdDFyrGkCE");
        InvalidYoutubeUrlException invalidYoutubeUrlException = assertThrows(InvalidYoutubeUrlException.class, () -> eventService.addEvent(event));
    }

    @Test
    void addEvent_withNullYoutubeUrl() {
        Event event = firstEvent();
        event.setId(null);
        event.setYoutubeId(null);

        when(eventRepository.saveAndFlush(event)).thenReturn(event);
        Event answer = eventService.addEvent(event);
    
        assertEvent(answer, event);
        verify(eventRepository, times(1)).saveAndFlush(event);
    }

    @Test
    void addEvent_withBlankYoutubeUrl() {
        Event event = firstEvent();
        event.setId(null);
        event.setYoutubeId("");

        when(eventRepository.saveAndFlush(event)).thenReturn(event);
        Event answer = eventService.addEvent(event);
    
        assertEvent(answer, event);
        verify(eventRepository, times(1)).saveAndFlush(event);
    }
    
    @Test
    void addEvent_withExistingId() {
        Event event = firstEvent();
        
        IdAlreadyExistsException idAlreadyExistsException = assertThrows(IdAlreadyExistsException.class, () -> eventService.addEvent(event));
        
        assertThat(idAlreadyExistsException).isNotNull();
        assertThat(idAlreadyExistsException.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(idAlreadyExistsException.getMessage()).isEqualTo("A new entity cannot already contain an id");
        assertThat(idAlreadyExistsException.getTitle()).isEqualTo("Cannot process entry");
        verifyNoInteractions(eventRepository);
    }
    
    @Test
    void editEvent() {
        Event firstEvent = firstEvent();
        Event secondEvent = secondEvent();
        secondEvent.setId(firstEvent.getId());
        
        when(eventRepository.saveAndFlush(secondEvent)).thenReturn(secondEvent);
        Event answer = eventService.editEvent(secondEvent.getId(), secondEvent);
    
        assertEvent(answer, secondEvent);
        verify(eventRepository, times(1)).saveAndFlush(secondEvent);
    }
    
    @Test
    void editEvent_withMismatchingId() {
        Event firstEvent = firstEvent();
        Event secondEvent = secondEvent();
        
        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, () -> eventService.editEvent(firstEvent.getId(), secondEvent));
        
        assertThat(idNotFoundException).isNotNull();
        assertThat(idNotFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(idNotFoundException.getTitle()).isEqualTo("Cannot process entry");
        assertThat(idNotFoundException.getMessage()).isEqualTo(String.format("Id of event to edit does not match given id. Event id = %d, and given id = %d", secondEvent.getId(), firstEvent.getId()));
        
        verify(eventRepository, times(0)).saveAndFlush(secondEvent);
    }
    
    @Test
    void findEventsByUserId() {
        Long id = 1L;
        User user = firstUser();
        when(userService.findById(any())).thenReturn(user);
        when(eventRepository.findAllByAttendeesContains(any(), any())).thenReturn(createPageOfEventsOfSize3());
        Page<Event> answer = eventService.findEventsByUserId(id, PAGE_REQUEST);
        
        assertPage(answer, createPageOfEventsOfSize3());
        verify(eventRepository, times(1)).findAllByAttendeesContains(any(),eq(PAGE_REQUEST));
    }

    @Test
    void findLikedEventsByUserId() {
        Long id = 1L;
        User user = firstUser();
        when(userService.findById(any())).thenReturn(user);
        when(eventRepository.findAllByUsersThatLikeThisEventContains(any(), any())).thenReturn(createPageOfEventsOfSize3());
        Page<Event> answer = eventService.findLikedEventsByUserId(id, PAGE_REQUEST);

        assertPage(answer, createPageOfEventsOfSize3());
        verify(eventRepository, times(1)).findAllByUsersThatLikeThisEventContains(any(),eq(PAGE_REQUEST));
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
