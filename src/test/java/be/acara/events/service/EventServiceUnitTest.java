package be.acara.events.service;

import be.acara.events.controller.dto.ApiError;
import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.exceptions.IdAlreadyExistsException;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.service.mapper.CategoryMapper;
import be.acara.events.service.mapper.EventMapper;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceUnitTest {
    
    @Mock
    private EventRepository repository;
    private EventService service;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        EventMapper eventMapper = new EventMapper();
        CategoryMapper categoryMapper = new CategoryMapper();
        service = new EventService(repository,eventMapper,categoryMapper);
    }

    @Test
    void findById() {
        Long idToFind = 1L;

        Mockito.when(repository.findById(idToFind)).thenReturn(Optional.of(firstEvent()));
        EventDto answer = service.findById(idToFind);
        
        assertEvent(EventUtil.map(answer));
        verify(repository, times(1)).findById(idToFind);
    }

    @Test
    void findAllByAscendingDate() {
        Mockito.when(repository.findAllByOrderByEventDateAsc()).thenReturn(EventUtil.createListsOfEvents_ofSize3());
        EventList answer = service.findAllByAscendingDate();
        
        assertEventList(answer);
        verify(repository, times(1)).findAllByOrderByEventDateAsc();
    }

    @Test
    void deleteEvent() {
        Event eventToDelete = firstEvent();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(eventToDelete));
        service.deleteEvent(1L);
        verify(repository, times(1)).delete(eventToDelete);
    }
    
    @Test
    void findById_notFound() {
        Long idToFind = Long.MAX_VALUE;
        Mockito.when(repository.findById(idToFind)).thenThrow(EventNotFoundException.class);
        assertThrows(EventNotFoundException.class, () -> service.findById(idToFind));
        verify(repository, times(1)).findById(idToFind);
    }
    
    @Test
    void search_emptyParams() {
        Map<String, String> params = new HashMap<>();
        EventList search = service.search(params);
        
        assertThat(search).isNotNull();
        assertThat(search.getEventList()).isNotNull();
        assertThat(search.getEventList()).isEmpty();
        
        verify(repository, times(0)).findAll();
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
        
        when(repository.findAll(any(Specification.class))).thenReturn(List.of(event));
        EventList search = service.search(params);
        
        assertEventList(search);
        verify(repository, times(1)).findAll(any(Specification.class));
    }
    
    @Test
    void getAllCategories() {
        CategoriesList answer = service.getAllCategories();
        
        assertThat(answer).isNotNull();
        assertThat(answer.getCategories()).isNotNull();
        assertThat(answer.getCategories().size()).isEqualTo(Category.values().length);
        
        List<String> listOfCategoryValues = Arrays.stream(Category.values()).map(Category::getWebDisplay).collect(Collectors.toList());
        assertThat(answer.getCategories()).isEqualTo(listOfCategoryValues);
        Mockito.verifyNoInteractions(repository);
    }
    
    @Test
    void addEvent() {
        Event newEvent = firstEvent();
        newEvent.setId(null);
        EventDto mappedDto = EventUtil.map(newEvent);
    
        when(repository.saveAndFlush(newEvent)).thenReturn(firstEvent());
        EventDto answer = service.addEvent(mappedDto);
        
        assertThat(answer).isEqualTo(EventUtil.map(firstEvent()));
        verify(repository, times(1)).saveAndFlush(newEvent);
    }
    
    @Test
    void addEvent_withExistingId() {
        Event newEvent = firstEvent();
        EventDto mappedDto = EventUtil.map(newEvent);
    
        ApiError apiError = ApiError.builder()
                .title("Cannot process entry")
                .code(422)
                .status(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                .message("A new entity cannot already contain an id")
                .build();
    
        IdAlreadyExistsException idAlreadyExistsException = assertThrows(IdAlreadyExistsException.class, () -> service.addEvent(mappedDto));
        
        assertThat(idAlreadyExistsException).isNotNull();
        assertThat(idAlreadyExistsException.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(idAlreadyExistsException.getMessage()).isEqualTo(apiError.getMessage());
        assertThat(idAlreadyExistsException.getTitle()).isEqualTo(apiError.getTitle());
        Mockito.verifyNoInteractions(repository);
    }
    
    @Test
    void editEvent() {
        Event firstEvent = firstEvent();
        Event secondEvent = secondEvent();
        secondEvent.setId(firstEvent.getId());
        
        when(repository.findById(firstEvent.getId())).thenReturn(Optional.of(firstEvent));
        when(repository.saveAndFlush(secondEvent)).thenReturn(secondEvent);
        EventDto answer = service.editEvent(secondEvent.getId(), map(secondEvent));
        
        assertThat(answer).isNotNull();
        assertThat(answer).isEqualTo(map(secondEvent));
        verify(repository, times(1)).findById(secondEvent.getId());
        verify(repository, times(1)).saveAndFlush(secondEvent);
    }
    
    @Test
    void editEvent_withMismatchingId() {
        Event firstEvent = firstEvent();
        Event secondEvent = secondEvent();
    
        when(repository.findById(firstEvent.getId())).thenReturn(Optional.of(firstEvent));
        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, () -> service.editEvent(firstEvent.getId(), map(secondEvent)));
        
        assertThat(idNotFoundException).isNotNull();
        assertThat(idNotFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(idNotFoundException.getTitle()).isEqualTo("Cannot process entry");
        assertThat(idNotFoundException.getMessage()).isEqualTo(String.format("Id of member to edit does not match given id. Member id = %d, and given id = %d", secondEvent.getId(), firstEvent.getId()));
        verify(repository, times(1)).findById(firstEvent.getId());
        verify(repository, times(0)).saveAndFlush(secondEvent);
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
        assertThat(eventList.getEventList().size()).isGreaterThanOrEqualTo(0);
        eventList.getEventList().stream().map(EventUtil::map).forEach(this::assertEvent);
    }
}
