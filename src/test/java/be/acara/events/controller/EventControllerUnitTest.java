package be.acara.events.controller;


import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.exceptions.IdAlreadyExistsException;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static be.acara.events.util.EventUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class EventControllerUnitTest {

    private EventController controller;
    private EventService service = Mockito.mock(EventService.class);
    private Validator validator;

    @BeforeEach
    void setUp() {
        controller = new EventController(service);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void findById() {
        long idToFind = 1L;
        Mockito.when(service.findById(idToFind)).thenReturn(map(firstEvent()));
        assertThat(controller.findById(idToFind)).isEqualTo(new ResponseEntity<>(map(firstEvent()), HttpStatus.OK));
    }

    @Test
    void findById_notFound(){
        long idToFind = Long.MAX_VALUE;
        Mockito.when(service.findById(idToFind)).thenThrow(new EventNotFoundException(String.format("Event with ID %d not found", idToFind)));
        EventNotFoundException answer = assertThrows(EventNotFoundException.class, () -> controller.findById(idToFind));
        
        assertThat(answer.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(answer.getMessage()).isEqualTo(String.format("Event with ID %d not found", idToFind));
        assertThat(answer.getTitle()).isEqualTo("Event not found");
    }

    @Test
    void findAll() {
        Mockito.when(service.findAllByAscendingDate()).thenReturn(createEventListOfSize3());
        ResponseEntity<EventList> answer = controller.findAllByAscendingDate();
        assertThat(answer).isEqualTo(new ResponseEntity<>(createEventListOfSize3(), HttpStatus.OK));
        assertThat(answer.getBody()).isEqualTo(createEventListOfSize3());
        assertThat(answer.getBody()).isNotNull();
        assertThat(answer.getBody().getEventList()).isNotNull();
        assertThat(answer.getBody().getEventList().size()).isEqualTo(3);
    }

    @Test
    void deleteById(){
        long idToDelete = 1L;
        Mockito.doNothing().when(service).deleteEvent(idToDelete);
        assertThat(controller.deleteEvent(idToDelete)).isEqualTo(new ResponseEntity<>("Event deleted succesfully",HttpStatus.NO_CONTENT));
    }
    
    @Test
    void addEvent() {
        EventDto eventDto = map(firstEvent());
        eventDto.setId(null);
        Mockito.when(service.addEvent(eventDto)).thenReturn(map(firstEvent()));

        ResponseEntity<EventDto> answer = controller.addEvent(eventDto);

        assertThat(answer).isEqualTo(ResponseEntity.created(URI.create(String.format("/api/events/%d",answer.getBody().getId()))).body(map(firstEvent())));
    }

    @Test
    void addEvent_existingId() {
        EventDto eventDto = map(firstEvent());
        Mockito.when(service.addEvent(eventDto)).thenThrow(IdAlreadyExistsException.class);
        assertThrows(IdAlreadyExistsException.class, () -> controller.addEvent(eventDto));
    }
    
    @Test
    void addEvent_testIfItHasViolations() {
        EventDto eventDto = map(firstEvent());
        eventDto.setId(null);
        eventDto.setEventDate(null);
        eventDto.setName("");
        Set<ConstraintViolation<EventDto>> violations = validator.validate(eventDto);
        assertThat(violations.size()).isEqualTo(2);
    }
    
    @Test
    void getAllCategories() {
        Mockito.when(service.getAllCategories()).thenReturn(new CategoriesList(List.of(Category.MUSIC.getWebDisplay())));
        ResponseEntity<CategoriesList> allCategories = controller.findAllCategories();
        assertThat(allCategories.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allCategories.getBody()).isNotNull();
        assertThat(allCategories.getBody().getCategories()).isNotNull();
        assertThat(allCategories.getBody().getCategories()).extracting(s -> s).containsAnyOf(Category.MUSIC.getWebDisplay());
    }

    @Test
    void editEvent() {
        EventDto eventDto = map(firstEvent());
        eventDto.setPrice(new BigDecimal("30"));
        Mockito.when(service.editEvent(eventDto.getId(),eventDto)).thenReturn(map(firstEvent()));
        ResponseEntity<EventDto> answer = controller.editEvent(eventDto.getId(), eventDto);
        assertThat(answer).isEqualTo(ResponseEntity.ok(map(firstEvent())));
    }

    @Test
    void editEvent_nonExistingId() {
        EventDto eventDto = map(firstEvent());
        eventDto.setId(Long.MAX_VALUE);
        Mockito.when(service.editEvent(eventDto.getId(), eventDto)).thenThrow(IdNotFoundException.class);
        assertThrows(IdNotFoundException.class, () -> controller.editEvent(eventDto.getId(),eventDto));
    }
    
    @Test
    void searchLocation() {
        Map<String,String> searchParams = new HashMap<>();
        searchParams.put("location","genk");
        EventList eventList = new EventList(List.of(map(secondEvent()), map(firstEvent())));
        Mockito.when(service.search(searchParams)).thenReturn(eventList);
        
        ResponseEntity<EventList> answer = controller.search(searchParams);
        
        assertThat(answer).isEqualTo(ResponseEntity.ok(eventList));
        assertThat(answer.getBody()).isNotNull();
        assertThat(answer.getBody().getEventList()).isNotNull();
    }
}
