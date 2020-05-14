package be.acara.events.service;

import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.domain.Event_;
import be.acara.events.exceptions.*;
import be.acara.events.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;

    @Autowired
    public EventServiceImpl(EventRepository repository, UserService userService) {
        this.eventRepository = repository;
        this.userService = userService;
    }

    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with ID %d not found", id)));
    }
    
    /**
     * This method will return all events in a Page<Event> format.
     *
     * Given no parameters or the sorting parameter being UNSORTED, the method will return all Events by eventDate
     * in Ascending order.
     *
     * Given a sorting parameter, the method will firstly make sure to ignoreCase() on all of them before returning the
     * results.
     *
     * @param pageable the specifications that the page needs to have
     * @return A page matching the specifications
     */
    @Override
    public Page<Event> findAll(Pageable pageable) {
        Sort sort = Sort.by("eventDate").ascending();
        if (pageable.getSort().isSorted()) {
            List<Sort.Order> collect = pageable.getSort().get().map(Sort.Order::ignoreCase).collect(Collectors.toList());
            sort = Sort.by(collect);
        }
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),sort);
        return eventRepository.findAll(pageRequest);
    }

    @Override
    public List<Category> getAllCategories() {
        return Arrays.stream(Category.values()).collect(Collectors.toList());
    }


    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(String.format("Event with ID %d not found", id));
        }
        eventRepository.deleteById(id);
    }

    @Override
    public Event addEvent(Event event) {
        if (event.getId() != null) {
            throw new IdAlreadyExistsException("A new entity cannot already contain an id");
        }

        String youtubeUrl = event.getYoutubeId();

        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            String[] youtubeIdArray = youtubeUrl.split("=");
            if (youtubeIdArray.length < 2) {
                throw new InvalidYoutubeUrlException("Invalid youtube URL");
            }
            String youtubeId = youtubeIdArray[1];
            event.setYoutubeId(youtubeId);
        }
        return eventRepository.saveAndFlush(event);
    }

    @Override
    public Event editEvent(Long id, Event event) {
        if (!event.getId().equals(id)) {
            throw new IdNotFoundException(String.format("Id of event to edit does not match given id. Event id = %d, and given id = %d", event.getId(), id));
        }
        return eventRepository.saveAndFlush(event);
    }

    @Override
    public Page<Event> findEventsByUserId(Long id, Pageable pageable) {
        return eventRepository.findAllByAttendeesContains(userService.findById(id), pageable);
    }

    /**
     * This method will use the Criteria API of JPA to search with. We will use Spring Data Specification to as
     * our provider.
     * <p>
     * The Criteria API is a flexible and type-safe alternative that requires writing or maintaining no SQL statements.
     * <p>
     * First we will check if params is null or empty, in which we return an empty {@link EventList}.
     * Next, we will create an empty or 'null' Specification<Event>. For each predetermined parameter, we will append
     * to our Specification using the and()-method.
     * <p>
     * If the parameter is defined and using Java 8 or higher, we will use Lambda-expressions to create the actual
     * query.
     * Taking CriteraBuilder.like() as an example, we will provide the root (our entity), specify the Path of the
     * variable using MetaModel and the value to check against.
     * <p>
     * The MetaModel is an entity class created during the mvn compile phase using hibernate-jpamodelgen dependency.
     * The class is generated with an underscore appended, like the generated Person_ is a metamodel of Person.
     *
     * @param params a hashmap of parameters
     * @return the eventlist containing all results
     */
    @Override
    public Page<Event> search(Map<String, String> params, Pageable pageable) {
        if (params == null || params.isEmpty()) {
            return Page.empty();
        }
        Specification<Event> specification = Specification.where(null);
        if (params.containsKey("location")) { //check if param exists
            specification = specification.and(
                    (root, cq, cb) ->
                            cb.like( //the where operator
                                    cb.lower(root.get(Event_.location)), // database value
                                    String.format("%%%s%%", params.get("location").toLowerCase()))); // the value to test
        }
        if (params.containsKey("minPrice")) {
            specification = specification.and(
                    (root, cq, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get(Event_.price),
                                    new BigDecimal(params.get("minPrice"))));
        }
        if (params.containsKey("maxPrice")) {
            specification = specification.and(
                    (root, cq, cb) ->
                            cb.lessThanOrEqualTo(
                                    root.get(Event_.price),
                                    new BigDecimal(params.get("maxPrice"))));
        }
        if (params.containsKey("startDate")) {
            specification = specification.and(
                    (root, cq, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get(Event_.eventDate),
                                    LocalDate.parse(params.get("startDate")).atStartOfDay()
                            )
            );
        }
        if (params.containsKey("endDate")) {
            specification = specification.and(
                    (root, cq, cb) ->
                            cb.lessThanOrEqualTo(
                                    root.get(Event_.eventDate),
                                    LocalDate.parse(params.get("endDate")).atStartOfDay()
                            )
            );
        }
        if (params.containsKey("category")) {
            specification = specification.and(
                    (root, cq, cb) ->
                            cb.equal(
                                    root.get(Event_.CATEGORY),
                                    Category.valueOf(params.get("category").toUpperCase())));
        }
        if (params.containsKey("name")){
            specification = specification.and(
                    ((root, cq, cb) ->
                            cb.like(
                                    root.get(Event_.name),
                                    String.format("%%%s%%", params.get("name").toLowerCase()))));
        }
        return eventRepository.findAll(specification, pageable);
    }

    @Override
    public Page<Event> findLikedEventsByUserId(Long id, Pageable pageable) {
        return eventRepository.findAllByUsersThatLikeThisEventContains(userService.findById(id), pageable);
    }

    @Override
    public Set<Event> mostPopularEvents() {
        Set<Event> mostPopularEvents =  eventRepository.findAll().stream()
                .sorted(Comparator.comparingInt((Event o) -> o.getAttendees().size()).reversed())
                .limit(4)
                .collect(Collectors.toSet());
        return mostPopularEvents;
    }
}
