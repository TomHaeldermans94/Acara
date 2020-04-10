package be.acara.events.controller;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Event;
import be.acara.events.service.EventService;
import be.acara.events.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    private EventMapper eventMapper;

    @Autowired
    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                    eventMapper.map(
                            eventService.findById(id)));
    }

    @GetMapping()
    public ResponseEntity<EventList> findAllByAscendingDate(Pageable pageable) {
        Page<Event> eventPage = eventService.findAllByAscendingDate(pageable);
        return ResponseEntity.ok(eventMapper.map(eventPage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoriesList> findAllCategories() {
        return ResponseEntity.ok(eventService.getAllCategories());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDto> addEvent(@RequestBody @Valid EventDto eventDto) {
        Event event = eventService.addEvent(eventMapper.map(eventDto));
        URI uri = URI.create(String.format("/api/events/%d", event.getId()));
        return ResponseEntity.created(uri).body(eventMapper.map(event));
    }
    
    @GetMapping("search")
    public ResponseEntity<EventList> search(@RequestParam Map<String,String> params, Pageable pageable) {
        Page<Event> search = eventService.search(params, pageable);
        return ResponseEntity.ok(eventMapper.map(search));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDto> editEvent(@PathVariable("id") Long id, @RequestBody @Valid EventDto eventDto) {
        Event event = eventService.editEvent(id, eventMapper.map(eventDto));
        return ResponseEntity.ok(eventMapper.map(event));
    }

    @GetMapping("/userevents/{id}")
    public ResponseEntity<EventList> findEventsByUserId(@PathVariable("id")Long id, Pageable pageable){
        return ResponseEntity.ok(eventMapper.map(eventService.findEventsByUserId(id, pageable)));
    }
}
