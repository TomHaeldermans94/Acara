package be.acara.events.service;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.repository.EventRepository;
import be.acara.events.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class EventService {

    private final EventRepository repository;
    private final EventMapper mapper;

    @Autowired
    public EventService(EventRepository repository, EventMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EventDto> getAllEvents() {
       return repository.findAll().stream().map(mapper::map).collect(Collectors.toList());
    }
}
