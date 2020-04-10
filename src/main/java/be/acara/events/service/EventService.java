package be.acara.events.service;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface EventService {
    EventDto findById(Long id);
    
    EventList findAllByAscendingDate(Pageable pageable);
    
    CategoriesList getAllCategories();
    
    void deleteEvent(Long id);
    
    EventDto addEvent(EventDto eventDto);
    
    EventDto editEvent(Long id, EventDto eventDto);
    
    EventList findEventsByUserId(Long id, Pageable pageable);
    
    EventList search(Map<String, String> params);
}
