package be.acara.events.controller;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
