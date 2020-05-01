package be.acara.events.controller;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.CategoryDto;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Event;
import be.acara.events.service.EventService;
import be.acara.events.service.mapper.CategoryMapper;
import be.acara.events.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;

    @Autowired
    public EventController(EventService eventService, EventMapper eventMapper, CategoryMapper categoryMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
        this.categoryMapper = categoryMapper;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                    eventMapper.eventToEventDto(
                            eventService.findById(id)));
    }

    @GetMapping()
    public ResponseEntity<EventList> findAllByAscendingDate(Pageable pageable) {
        Page<Event> eventPage = eventService.findAll(pageable);
        return ResponseEntity.ok(eventMapper.pageToEventList(eventPage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoriesList> findAllCategories() {
        List<CategoryDto> categoryDtos = eventService.getAllCategories().stream().map(categoryMapper::categoryToCategoryDto).collect(Collectors.toList());
        return ResponseEntity.ok(new CategoriesList(categoryDtos));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDto> addEvent(@RequestBody @Valid EventDto eventDto) {
        Event event = eventService.addEvent(eventMapper.eventDtoToEvent(eventDto));
        URI uri = URI.create(String.format("/api/events/%d", event.getId()));
        return ResponseEntity.created(uri).body(eventMapper.eventToEventDto(event));
    }
    
    @GetMapping("search")
    public ResponseEntity<EventList> search(@RequestParam Map<String,String> params, Pageable pageable) {
        Page<Event> search = eventService.search(params, pageable);
        return ResponseEntity.ok(eventMapper.pageToEventList(search));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EventDto> editEvent(@PathVariable("id") Long id, @RequestBody @Valid EventDto eventDto) {
        Event event = eventService.editEvent(id, eventMapper.eventDtoToEvent(eventDto));
        return ResponseEntity.ok(eventMapper.eventToEventDto(event));
    }

    @GetMapping("/userevents/{id}")
    public ResponseEntity<EventList> findEventsByUserId(@PathVariable("id")Long id, Pageable pageable){
        return ResponseEntity.ok(eventMapper.pageToEventList(eventService.findEventsByUserId(id, pageable)));
    }

    @GetMapping("/likedevents/{id}")
    public ResponseEntity<EventList> findLikedEventsByUserId(@PathVariable("id")Long id, Pageable pageable){
        return ResponseEntity.ok(eventMapper.pageToEventList(eventService.findLikedEventsByUserId(id, pageable)));
    }

    @PutMapping("/like/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<EventDto> likeEvent(@PathVariable("id") Long id) {
        eventService.likeEvent(id);
        return ResponseEntity.noContent().build();
    }
}
