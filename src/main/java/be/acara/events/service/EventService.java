package be.acara.events.service;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.exceptions.BadRequestException;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.exceptions.IdAlreadyExistsException;
import be.acara.events.repository.EventRepository;
import be.acara.events.service.mapper.CategoryMapper;
import be.acara.events.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class EventService {

    private final EventRepository repository;
    private final EventMapper mapper;
    private final CategoryMapper categoryMapper;

    @Autowired
    public EventService(EventRepository repository, EventMapper mapper, CategoryMapper categoryMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.categoryMapper = categoryMapper;
    }

    public EventDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::map)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with ID %d not found", id)));
    }

    public EventList findAllByAscendingDate() {
        return new EventList(mapper.mapEntityListToDtoList(repository.findAllByOrderByEventDateAsc()));
    }

    public CategoriesList getAllCategories() {
        return categoryMapper.map(Category.values());
    }

    public void deleteEvent(long id) {
        Event event = getEvent(id);
        if(event.getId() == id){
            repository.delete(event);
        }
    }

    private Event getEvent(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with ID %d not found", id)));
    }

    public EventDto addEvent(EventDto eventDto) {
        if (eventDto.getId() != null) {
            throw new IdAlreadyExistsException("A new entity cannot already contain an id");
        }
        Event event = mapper.map(eventDto);
        return mapper.map(repository.saveAndFlush(event));
    }

    public EventDto editEvent(EventDto eventDto, long id) {
        Event eventToEdit = getEvent(id);
        if(eventDto.getId().equals(eventToEdit.getId())){
            Event event = mapper.map(eventDto);
            return mapper.map(repository.saveAndFlush(event));
        }
        else {
            throw new BadRequestException(
                    "Bad request on editing member",
                    String.format(
                            "Id of member to edit does not match given id. Member id = %d, and given id = %d", eventDto.getId(), id)
            );
        }
    }
}
