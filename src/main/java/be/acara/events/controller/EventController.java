package be.acara.events.controller;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @GetMapping()
    public ResponseEntity<EventList> findAllByAscendingDate() {
        return ResponseEntity.ok(eventService.findAllByAscendingDate());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteEvent(@PathVariable("id") long id) {
        eventService.deleteEvent(id);
        return new ResponseEntity<>("Event deleted succesfully", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoriesList> findAllCategories() {
        return ResponseEntity.ok(eventService.getAllCategories());
    }

    @PostMapping("/new-event")
    public ResponseEntity<EventDto> addEvent(@RequestBody @Valid EventDto event) {
        EventDto eventDto = eventService.addEvent(event);
        URI uri = URI.create("/api/events/new-event");
        return ResponseEntity.created(uri).body(eventDto);
    }

    @PostMapping("/edit-event/{id}")
    public ResponseEntity<EventDto> editEvent(@PathVariable("id") long id, @RequestBody @Valid EventDto event) {
        EventDto eventDto = eventService.editEvent(event, id);
        URI uri = URI.create(String.format("/api/events/edit-event/%d", eventDto.getId()));
        return ResponseEntity.created(uri).body(eventDto);
    }
}
