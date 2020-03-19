package be.acara.events.controller;


import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        Mockito.when(service.findById(idToFind)).thenReturn(createEventDto());
        assertThat(controller.findById(idToFind)).isEqualTo(new ResponseEntity<>(createEventDto(), HttpStatus.OK));
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
        assertThat(controller.findAllByAscendingDate()).isEqualTo(new ResponseEntity<>(createEventDtoListForDateTesting(), HttpStatus.OK));
    }

    @Test
    void deleteById(){
        long idToDelete = 1L;
        Mockito.doNothing().when(service).deleteEvent(idToDelete);
        assertThat(controller.deleteEvent(idToDelete)).isEqualTo(new ResponseEntity<>("Event deleted succesfully",HttpStatus.NO_CONTENT));
    }
    
    @Test
    void addEvent() {
        EventDto eventDto = createEventDto();
        eventDto.setId(null);
        Mockito.when(service.addEvent(eventDto)).thenReturn(createEventDto());

        ResponseEntity<EventDto> answer = controller.addEvent(eventDto);

        assertThat(answer).isEqualTo(ResponseEntity.created(URI.create(String.format("/api/events/%d",answer.getBody().getId()))).body(createEventDto()));
    }

    @Test
    void addEvent_existingId() {
        EventDto eventDto = createEventDto();
        Mockito.when(service.addEvent(eventDto)).thenThrow(IdAlreadyExistsException.class);
        assertThrows(IdAlreadyExistsException.class, () -> controller.addEvent(eventDto));
    }
    
    @Test
    void addEvent_testIfItHasViolations() {
        EventDto eventDto = createEventDto();
        eventDto.setId(null);
        eventDto.setEventDate(null);
        eventDto.setName("");
        Set<ConstraintViolation<EventDto>> violations = validator.validate(eventDto);
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    void editEvent() {
        EventDto eventDto = createEventDto();
        eventDto.setPrice(new BigDecimal("30"));
        Mockito.when(service.editEvent(eventDto.getId(),eventDto)).thenReturn(createEventDto());
        ResponseEntity<EventDto> answer = controller.editEvent(eventDto.getId(), eventDto);
        assertThat(answer).isEqualTo(ResponseEntity.ok(createEventDto()));
    }

    @Test
    void editEvent_nonExistingId() {
        EventDto eventDto = createEventDto();
        eventDto.setId(Long.MAX_VALUE);
        Mockito.when(service.editEvent(eventDto.getId(), eventDto)).thenThrow(IdNotFoundException.class);
        assertThrows(IdNotFoundException.class, () -> controller.editEvent(eventDto.getId(),eventDto));
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
