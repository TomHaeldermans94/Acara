package be.acara.events.service;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.exceptions.IdAlreadyExistsException;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.service.mapper.EventMapper;
import be.acara.events.service.mapper.UserMapper;
import be.acara.events.util.EventUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static be.acara.events.util.EventUtil.*;
import static be.acara.events.util.UserUtil.firstUser;
import static be.acara.events.util.UserUtil.firstUserDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceUnitTest {
    
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        EventMapper eventMapper = new EventMapper();
        UserMapper userMapper = new UserMapper();
        eventService = new EventService(eventRepository, userService, eventMapper, userMapper);
    }

    @Test
    void findById() {
        Long idToFind = 1L;

        Mockito.when(eventRepository.findById(idToFind)).thenReturn(Optional.of(firstEvent()));
        EventDto answer = eventService.findById(idToFind);
        
        assertEvent(EventUtil.map(answer));
        verify(eventRepository, times(1)).findById(idToFind);
    }

    @Test
    void findAllByAscendingDate() {
        Mockito.when(eventRepository.findAllByOrderByEventDateAsc()).thenReturn(EventUtil.createListsOfEventsOfSize3());
        EventList answer = eventService.findAllByAscendingDate();
        
        assertEventList(answer);
        verify(eventRepository, times(1)).findAllByOrderByEventDateAsc();
    }

    @Test
    void deleteEvent() {
        Event eventToDelete = firstEvent();
        Mockito.when(eventRepository.existsById(1L)).thenReturn(true);
        eventService.deleteEvent(firstEvent().getId());
        verify(eventRepository, times(1)).deleteById(eventToDelete.getId());
    }
    
    @Test
    void deleteEvent_notFound() {
        Long id = 1L;
        Mockito.when(eventRepository.existsById(anyLong())).thenReturn(false);
        EventNotFoundException eventNotFoundException = assertThrows(EventNotFoundException.class, () -> eventService.deleteEvent(firstEvent().getId()));
        
        assertThat(eventNotFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(eventNotFoundException.getTitle()).isEqualTo("Event not found");
        assertThat(eventNotFoundException.getMessage()).isEqualTo(String.format("Event with ID %d not found", id));
        
        verify(eventRepository, times(1)).existsById(id);
    }
    
    @Test
    void findById_notFound() {
        Long idToFind = Long.MAX_VALUE;
        Mockito.when(eventRepository.findById(idToFind)).thenReturn(Optional.empty());
        
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
        EventList search = eventService.search(params);
        
        assertThat(search).isNotNull();
        assertThat(search.getEventDtoList()).isNotNull();
        assertThat(search.getEventDtoList()).isEmpty();
        
        verify(eventRepository, times(0)).findAll();
    }
    
    @Test
    void search_withParams() {
        Map<String, String> params = new HashMap<>();
        Event event = firstEvent();
        params.put("location",event.getLocation());
        params.put("minPrice",event.getPrice().toString());
        params.put("maxPrice",event.getPrice().toString());
        params.put("startDate",event.getEventDate().toString());
        params.put("endDate",event.getEventDate().toString());
        when(eventRepository.findAll(any(Specification.class))).thenReturn(List.of(event));
        EventList search = eventService.search(params);
        
        assertEventList(search);
        verify(eventRepository, times(1)).findAll(any(Specification.class));
    }
    
    @Test
    void getAllCategories() {
        CategoriesList answer = eventService.getAllCategories();
        
        assertThat(answer).isNotNull();
        assertThat(answer.getCategories()).isNotNull();
        assertThat(answer.getCategories().size()).isEqualTo(Category.values().length);
        
        List<String> listOfCategoryValues = Arrays.stream(Category.values()).map(Category::getWebDisplay).collect(Collectors.toList());
        assertThat(answer.getCategories()).isEqualTo(listOfCategoryValues);
        Mockito.verifyNoInteractions(eventRepository);
    }
    
    @Test
    void addEvent() {
        Event newEvent = firstEvent();
        newEvent.setId(null);
        EventDto mappedDto = EventUtil.map(newEvent);
    
        when(eventRepository.saveAndFlush(newEvent)).thenReturn(firstEvent());
        EventDto answer = eventService.addEvent(mappedDto);
        
        assertThat(answer).isEqualTo(EventUtil.map(firstEvent()));
        verify(eventRepository, times(1)).saveAndFlush(newEvent);
    }
    
    @Test
    void addEvent_withExistingId() {
        Event newEvent = firstEvent();
        EventDto mappedDto = EventUtil.map(newEvent);
    
        IdAlreadyExistsException idAlreadyExistsException = assertThrows(IdAlreadyExistsException.class, () -> eventService.addEvent(mappedDto));
        
        assertThat(idAlreadyExistsException).isNotNull();
        assertThat(idAlreadyExistsException.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(idAlreadyExistsException.getMessage()).isEqualTo("A new entity cannot already contain an id");
        assertThat(idAlreadyExistsException.getTitle()).isEqualTo("Cannot process entry");
        Mockito.verifyNoInteractions(eventRepository);
    }
    
    @Test
    void editEvent() {
        Event firstEvent = firstEvent();
        Event secondEvent = secondEvent();
        secondEvent.setId(firstEvent.getId());
        
        when(eventRepository.findById(firstEvent.getId())).thenReturn(Optional.of(firstEvent));
        when(eventRepository.saveAndFlush(secondEvent)).thenReturn(secondEvent);
        EventDto answer = eventService.editEvent(secondEvent.getId(), map(secondEvent));
        
        assertThat(answer).isNotNull();
        assertThat(answer).isEqualTo(map(secondEvent));
        verify(eventRepository, times(1)).findById(secondEvent.getId());
        verify(eventRepository, times(1)).saveAndFlush(secondEvent);
    }
    
    @Test
    void editEvent_withMismatchingId() {
        Event firstEvent = firstEvent();
        Event secondEvent = secondEvent();
    
        when(eventRepository.findById(firstEvent.getId())).thenReturn(Optional.of(firstEvent));
        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, () -> eventService.editEvent(firstEvent.getId(), map(secondEvent)));
        
        assertThat(idNotFoundException).isNotNull();
        assertThat(idNotFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(idNotFoundException.getTitle()).isEqualTo("Cannot process entry");
        assertThat(idNotFoundException.getMessage()).isEqualTo(String.format("Id of member to edit does not match given id. Member id = %d, and given id = %d", secondEvent.getId(), firstEvent.getId()));
        verify(eventRepository, times(1)).findById(firstEvent.getId());
        verify(eventRepository, times(0)).saveAndFlush(secondEvent);
    }

    @Test
    void findEventsByUserId() {
        Long id = 1L;
        List<Event> events = createListsOfEventsOfSize3();
        UserDto userDto = firstUserDto();
        User user = firstUser();
        Mockito.when(userService.findById(any())).thenReturn(userDto);
        Mockito.when(eventRepository.findAllByAttendeesContains(any())).thenReturn(events);
        EventList answer = eventService.findEventsByUserId(id);

        assertEventList(answer);
        verify(eventRepository, times(1)).findAllByAttendeesContains(any());
    }
    

    private void assertEvent(Event event) {
        assertThat(event).isNotNull();
        assertThat(event.getId()).isNotNull();
        assertThat(event.getEventDate()).isAfterOrEqualTo(LocalDateTime.now());
        assertThat(event.getPrice()).isGreaterThanOrEqualTo(BigDecimal.ONE);
        assertThat(event.getImage()).isNotNull();
        assertThat(event.getLocation()).isNotNull();
        assertThat(event.getCategory()).isNotNull();
        assertThat(event.getDescription()).isNotNull();
        assertThat(event.getName()).isNotBlank();
    }
    
    private void assertEventList(EventList eventList) {
        assertThat(eventList).isNotNull();
        assertThat(eventList.getEventDtoList().size()).isGreaterThanOrEqualTo(0);
        eventList.getEventDtoList().stream().map(EventUtil::map).forEach(this::assertEvent);
    }
}
