package be.acara.events.controller;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoriesList> findAllCategories() {
        return ResponseEntity.ok(eventService.getAllCategories());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventDto> addEvent(@RequestBody @Valid EventDto event) {
        EventDto eventDto = eventService.addEvent(event);
        URI uri = URI.create(String.format("/api/events/%d", eventDto.getId()));
        return ResponseEntity.created(uri).body(eventDto);
    }
    
    @GetMapping("search")
    public ResponseEntity<EventList> search(@RequestParam Map<String,String> params) {
        return ResponseEntity.ok(eventService.search(params));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventDto> editEvent(@PathVariable("id") Long id, @RequestBody @Valid EventDto event) {
        EventDto eventDto = eventService.editEvent(id, event);
        return ResponseEntity.ok(eventDto);
    }
}
