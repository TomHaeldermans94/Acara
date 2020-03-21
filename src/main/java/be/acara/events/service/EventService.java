package be.acara.events.service;

import be.acara.events.controller.dto.CategoriesList;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.domain.Event_;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.exceptions.IdAlreadyExistsException;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.service.mapper.CategoryMapper;
import be.acara.events.service.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;


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
        if (event.getId() == id) {
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

    public EventDto editEvent(long id, EventDto eventDto) {
        EventDto eventToEdit = findById(id);
        if(!eventDto.getId().equals(eventToEdit.getId())){
            throw new IdNotFoundException(String.format("Id of member to edit does not match given id. Member id = %d, and given id = %d", eventDto.getId(), id)
            );
        }
        Event event = mapper.map(eventDto);
        return mapper.map(repository.saveAndFlush(event));
    }

    /**
     * This method will use the Criteria API of JPA to search with. We will use Spring Data Specification to as
     * our provider.
     *
     * The Criteria API is a flexible and type-safe alternative that requires writing or maintaining no SQL statements.
     *
     * First we will check if params is null or empty, in which we return an empty {@link EventList}.
     * Next, we will create an empty or 'null' Specification<Event>. For each predetermined parameter, we will append
     * to our Specification using the and()-method.
     *
     * If the parameter is defined and using Java 8 or higher, we will use Lambda-expressions to create the actual
     * query.
     * Taking CriteraBuilder.like() as an example, we will provide the root (our entity), specify the Path of the
     * variable using MetaModel and the value to check against.
     *
     * The MetaModel is an entity class created during the mvn compile phase using hibernate-jpamodelgen dependency.
     * The class is generated with an underscore appended, like the generated Person_ is a metamodel of Person.
     *
     * @param params a hashmap of parameters
     * @return the eventlist containing all results
     */
    public EventList search(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return new EventList();
        }
        Specification<Event> specification = Specification.where(null);
        if (params.containsKey("location")) { //check if param exists
            specification = specification.and(
                    (root, cq, cb) ->
                            cb.like( //the where operator
                                    cb.lower(root.get(Event_.location)), // database value
                                    String.format("%%%s%%", params.get("location").toLowerCase()))); // the value to test
        }
        return new EventList(mapper.mapEntityListToDtoList(repository.findAll(specification)));
    }
}
