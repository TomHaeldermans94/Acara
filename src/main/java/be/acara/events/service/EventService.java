package be.acara.events.service;

import be.acara.events.controller.dto.EventList;
import be.acara.events.domain.Category;
import be.acara.events.domain.Event;
import be.acara.events.exceptions.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

public interface EventService {
    /**
     * Find an id with matching id.
     *
     * @param id the id of the event
     * @return an Event with the corresponding id.
     * @throws EventNotFoundException if no event is found with the matching id.
     */
    Event findById(Long id);
    
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
    Page<Event> findAll(Map<String, String> params, Pageable pageable);
    
    /**
     * Returns a list of all categories.
     *
     * @return a list of {@link Category}
     */
    List<Category> getAllCategories();
    
    /**
     * Deletes an event with the specified id
     *
     * @param id the id of the event to delete
     * @throws EventNotFoundException if no event is found with the matching id.
     */
    void deleteEvent(Long id);
    
    /**
     * Adds an event to the repository
     *
     * @param event the event to create
     * @return the event after being processed
     * @throws IdAlreadyExistsException   if the to-be-created event arrives at this method with an id present
     * @throws InvalidDateException       if the event's date is in the past
     * @throws InvalidYoutubeUrlException if the youtube url is invalid
     */
    Event addEvent(Event event);
    
    /**
     * Edits an event
     *
     * @param id    the id of the event to edit
     * @param event the new body of the event
     * @return the edited event
     * @throws IdNotFoundException if the id and event are not matching
     */
    Event editEvent(Long id, Event event);
    
    /**
     * Finds all events the user, through it's id, subscribed to.
     *
     * @param id       the id of the user
     * @param pageable a paging and sorting parameter
     * @return a page containing all events that the user subscribed to.
     */
    Page<Event> findEventsByUserId(Long id, Pageable pageable);
    
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
    Specification<Event> createSpecification(Map<String, String> params);
    
    /**
     * Find all liked events from the specified user
     *
     * @param id       the user id
     * @param pageable a paging and sorting parameter
     * @return a page of all liked events
     */
    Page<Event> findLikedEventsByUserId(Long id, Pageable pageable);
    
    /**
     * Finds the most popular, filtered through the most subscribed, events.
     *
     * @return a list of size 4 of the most popular events
     */
    List<Event> mostPopularEvents();
    
    /**
     * Finds the next 2 occurring events in chronological order
     *
     * @return a list of size 2 of the next occuring events
     */
    List<Event> nextAttendingEvents();
    
    /**
     * Finds other events of similar categories. The given event will not be shown again in the returned list.
     *
     * @param event the event to find other related events of
     * @return a list of size 2 that contains 2 other events that are related
     */
    List<Event> relatedEvents(Event event);
}
