package be.acara.events.service;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventService {

    private final EventRepository repository;
    private final EventMapper mapper;

    @Autowired
    public EventService(EventRepository repository, EventMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public EventDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::map)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with ID %d not found", id)));
    }
}
