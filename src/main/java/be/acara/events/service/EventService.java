package be.acara.events.service;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface EventService {
    Event findById(Long id);
    
    Page<Event> findAllByAscendingDate(Pageable pageable);
    
    CategoriesList getAllCategories();
    
    void deleteEvent(Long id);
    
    Event addEvent(Event event);
    
    Event editEvent(Long id, Event event);
    
    Page<Event> findEventsByUserId(Long id, Pageable pageable);
    
    Page<Event> search(Map<String, String> params, Pageable pageable);
}
