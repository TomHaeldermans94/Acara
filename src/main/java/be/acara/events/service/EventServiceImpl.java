package be.acara.events.service;

import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.domain.Event_;
import be.acara.events.domain.User;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Business logic related to {@link Event}
 */
@Service
public class EventServiceImpl implements EventService {
    
    private final EventRepository eventRepository;
    private final UserService userService;
    
    @Autowired
    public EventServiceImpl(EventRepository repository, UserService userService) {
        this.eventRepository = repository;
        this.userService = userService;
    }
    
    /**
     * Find an id with matching id.
     *
     * @param id the id of the event
     * @return an Event with the corresponding id.
     * @throws EventNotFoundException if no event is found with the matching id.
     */
    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with ID %d not found", id)));
    }
    
    /**
     * This method will return all events in a {@code Page<Event>} format.
     * <p>
     * Given no parameters or the sorting parameter being UNSORTED, the method will return all Events by eventDate
     * in Ascending order.
     * <p>
     * Given a sorting parameter, the method will firstly make sure to ignoreCase() on all of them before returning the
     * results.
     *
     * @param pageable the specifications that the page needs to have
     * @return A page matching the specifications
     */
    @Override
    public Page<Event> findAll(Map<String, String> params, Pageable pageable) {
        Sort sort = Sort.by("eventDate").ascending();
        if (pageable.getSort().isSorted()) {
            List<Sort.Order> collect = pageable.getSort().get().map(Sort.Order::ignoreCase).collect(Collectors.toList());
            sort = Sort.by(collect);
        }
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return eventRepository.findAll(createSpecification(params), pageRequest);
    }
    
    /**
     * Returns a list of all categories.
     *
     * @return a list of {@link Category}
     */
    @Override
    public List<Category> getAllCategories() {
        return Arrays.stream(Category.values()).collect(Collectors.toList());
    }
    
    
    /**
     * Deletes an event with the specified id
     *
     * @param id the id of the event to delete
     * @throws EventNotFoundException if no event is found with the matching id.
     */
    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(String.format("Event with ID %d not found", id));
        }
        eventRepository.deleteById(id);
    }
    
    /**
     * Adds an event to the repository
     *
     * @param event the event to create
     * @return the event after being processed
     * @throws IdAlreadyExistsException   if the to-be-created event arrives at this method with an id present
     * @throws InvalidDateException       if the event's date is in the past
     * @throws InvalidYoutubeUrlException if the youtube url is invalid
     */
    @Override
    public Event addEvent(Event event) {
        if (event.getId() != null) {
            throw new IdAlreadyExistsException("A new entity cannot already contain an id");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new InvalidDateException("Date has to be in the present or future");
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
    
    /**
     * Edits an event
     *
     * @param id    the id of the event to edit
     * @param event the new body of the event
     * @return the edited event
     * @throws IdNotFoundException if the id and event are not matching
     */
    @Override
    public Event editEvent(Long id, Event event) {
        if (!event.getId().equals(id)) {
            throw new IdNotFoundException(String.format("Id of event to edit does not match given id. Event id = %d, and given id = %d", event.getId(), id));
        }
        return eventRepository.saveAndFlush(event);
    }
    
    /**
     * Finds all events the user, through it's id, subscribed to.
     *
     * @param id       the id of the user
     * @param pageable a paging and sorting parameter
     * @return a page containing all events that the user subscribed to.
     */
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
     * Next, we will create an empty or 'null' {@code Specification<Event>}. For each predetermined parameter, we will append
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
     * @param params a Map of parameters
     * @return the Specification with all the provided arguments specified
     */
    @Override
    public Specification<Event> createSpecification(Map<String, String> params) {
        Specification<Event> specification = Specification.where(
                (root, cq, cb) ->
                        cb.greaterThanOrEqualTo(
                                root.get(Event_.eventDate),
                                LocalDateTime.now()
                        )
        );
        
        if (params.containsKey("startDate")) {
            specification = specification.and(
                    (root, cq, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get(Event_.eventDate),
                                    LocalDate.parse(params.get("startDate")).atStartOfDay()
                            )
            );
        }
        
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
        if (params.containsKey("name")) {
            specification = specification.and(
                    ((root, cq, cb) ->
                            cb.like(
                                    cb.lower(root.get(Event_.name)),
                                    String.format("%%%s%%", params.get("name").toLowerCase())))); // the value to test
        }
        return specification;
    }
    
    /**
     * Find all liked events from the specified user
     *
     * @param id       the user id
     * @param pageable a paging and sorting parameter
     * @return a page of all liked events
     */
    @Override
    public Page<Event> findLikedEventsByUserId(Long id, Pageable pageable) {
        return eventRepository.findAllByUsersThatLikeThisEventContains(userService.findById(id), pageable);
    }
    
    /**
     * Finds the most popular, filtered through the most subscribed, events.
     *
     * @return a list of size 4 of the most popular events
     */
    @Override
    public List<Event> mostPopularEvents() {
        return eventRepository.findAll().stream()
                .sorted(Comparator.comparingInt((Event o) -> o.getAttendees().size()).reversed())
                .limit(4)
                .collect(Collectors.toList());
    }
    
    /**
     * Finds the next 2 occurring events in chronological order
     *
     * @return a list of size 2 of the next occuring events
     */
    @Override
    public List<Event> nextAttendingEvents() {
        User user = userService.getCurrentUser();
        return eventRepository.findAll().stream()
                .filter(event -> event.getAttendees().contains(user))
                .limit(2)
                .collect(Collectors.toList());
    }
    
    /**
     * Finds other events of similar categories. The given event will not be shown again in the returned list.
     *
     * @param event the event to find other related events of
     * @return a list of size 2 that contains 2 other events that are related
     */
    @Override
    public List<Event> relatedEvents(Event event) {
        return eventRepository.findAll().stream()
                .filter(e -> e.getCategory() == event.getCategory() && e != event)
                .limit(2)
                .collect(Collectors.toList());
    }
}
