package be.acara.events.controller;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static be.acara.events.util.EventUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventControllerTest {
    
    private EventController eventController;
    
    @Mock
    private EventService eventService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        eventController = new EventController(eventService);
    }
    
    @Test
    void findById() {
        Long id = 1L;
        when(eventService.findById(id)).thenReturn(map(firstEvent()));
    
        ResponseEntity<EventDto> answer = eventController.findById(id);
        
        assertResponseEntity(answer);
        assertEvent(answer.getBody(), map(firstEvent()));
        
        verifyOnce().findById(id);
    }
    
    @Test
    void findAllByAscendingDate() {
        when(eventService.findAllByAscendingDate()).thenReturn(createEventListOfSize3());
    
        ResponseEntity<EventList> answer = eventController.findAllByAscendingDate();
    
        assertResponseEntity(answer);
        assertEventList(answer.getBody(), createEventListOfSize3());
        
        verifyOnce().findAllByAscendingDate();
    }
    
    @Test
    void deleteEvent() {
        Long id = 1L;
        doNothing().when(eventService).deleteEvent(id);
    
        ResponseEntity<Void> responseEntity = eventController.deleteEvent(id);
        
        assertResponseEntity(responseEntity, HttpStatus.NO_CONTENT);
        
        verifyOnce().deleteEvent(id);
    }
    
    @Test
    void findAllCategories() {
        CategoriesList categoriesList = new CategoriesList(
                List.of(
                        Category.THEATRE.getWebDisplay(),
                        Category.MUSIC.getWebDisplay()
                )
        );
        when(eventService.getAllCategories()).thenReturn(categoriesList);
    
        ResponseEntity<CategoriesList> answer = eventController.findAllCategories();
        
        assertResponseEntity(answer);
        assertCategoriesList(answer.getBody(), categoriesList);
    
        verifyOnce().getAllCategories();
    }
    
    @Test
    void addEvent() {
        when(eventService.addEvent(map(firstEvent()))).thenReturn(map(firstEvent()));
    
        ResponseEntity<EventDto> answer = eventController.addEvent(map(firstEvent()));
        
        assertResponseEntity(answer, HttpStatus.CREATED);
        assertThat(answer.getHeaders().getLocation()).isEqualTo(URI.create("/api/events/1"));
        assertEvent(answer.getBody(),map(firstEvent()));
        
        verifyOnce().addEvent(map(firstEvent()));
    }
    
    @Test
    void searchEvent() {
        when(eventService.search(anyMap())).thenReturn(new EventList());
    
        ResponseEntity<EventList> answer = eventController.search(Collections.emptyMap());
        
        assertResponseEntity(answer);
        assertEventList(answer.getBody(),new EventList());
        
        verifyOnce().search(Collections.emptyMap());
    }
    
    @Test
    void editEvent() {
        EventDto event = map(firstEvent());
        when(eventService.editEvent(event.getId(), event)).thenReturn(event);
        
        ResponseEntity<EventDto> answer = eventController.editEvent(event.getId(), event);
        
        assertResponseEntity(answer);
        assertEvent(answer.getBody(), event);
        
        verifyOnce().editEvent(firstEvent().getId(), event);
    }
    
    private void assertEventList(EventList response, EventList expected) {
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expected);
    }
    
    private void assertCategoriesList(CategoriesList response, CategoriesList expected) {
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expected);
    }
    
    private void assertResponseEntity(ResponseEntity<?> answer, HttpStatus httpStatus) {
        assertThat(answer).isNotNull();
        assertThat(answer.getStatusCode()).isEqualTo(httpStatus);
        assertThat(answer.getStatusCodeValue()).isEqualTo(httpStatus.value());
        assertThat(answer.getBody()).isNotNull();
    }
    
    private void assertResponseEntity(ResponseEntity<?> answer) {
        assertResponseEntity(answer, HttpStatus.OK);
    }
    
    private void assertEvent(EventDto response, EventDto expected) {
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expected);
    }
    
    private EventService verifyOnce() {
        return verify(eventService, times(1));
    }
}
