package be.acara.events.service;

import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface EventService {
    Event findById(Long id);
    
    Page<Event> findAll(Pageable pageable);
    
    List<Category> getAllCategories();
    
    void deleteEvent(Long id);
    
    Event addEvent(Event event);
    
    Event editEvent(Long id, Event event);
    
    Page<Event> findEventsByUserId(Long id, Pageable pageable);
    
    Page<Event> search(Map<String, String> params, Pageable pageable);

    Page<Event> findLikedEventsByUserId(Long id, Pageable pageable);

    Set<Event> mostPopularEvents();

    Set<Event> relatedEvents(Event event);
}
