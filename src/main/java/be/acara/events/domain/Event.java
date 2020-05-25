package be.acara.events.domain;

import be.acara.events.domain.converter.CategoryConverter;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * The domain and entity class for an Event
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode
public class Event {
    
    /**
     * The id of the event
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The amount of times it has been liked
     */
    private int amountOfLikes;
    
    /**
     * The date and time of when this event occurs
     */
    private LocalDateTime eventDate;
    
    /**
     * The event name
     * <p>
     * Requires a value between 2 and 40 characters long
     */
    @Length(min = 2, max = 40)
    private String name;
    
    /**
     * The description text of an event
     * <p>
     * Maximum 2048 characters long
     */
    @Length(max = 2048)
    private String description;
    
    /**
     * The image of the event
     */
    @Lob
    private byte[] image;
    
    /**
     * Where the event takes place
     * <p>
     * Requires a value between 2 and 40 characters long
     */
    @Length(min = 2, max = 40)
    private String location;
    
    /**
     * Which category this event belongs to
     */
    @NotNull
    @Convert(converter = CategoryConverter.class)
    private Category category;
    
    /**
     * A set of all attending users
     */
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "EVENT_ATTENDEES",
            joinColumns = @JoinColumn(name = "EVENT_ID", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ATTENDEES_ID", referencedColumnName = "id"))
    @EqualsAndHashCode.Exclude
    private Set<User> attendees;
    
    /**
     * A set of all users that like this event
     */
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(name = "EVENT_USERS_THAT_LIKE_THIS_EVENT",
            joinColumns = @JoinColumn(name = "EVENT_ID", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "USERS_THAT_LIKE_THIS_EVENT_ID", referencedColumnName = "id"))
    @EqualsAndHashCode.Exclude
    private Set<User> usersThatLikeThisEvent;
    
    /**
     * The price of this event
     */
    private BigDecimal price;
    
    /**
     * The youtube id that the user provided upon creation of this event
     */
    private String youtubeId;
    
    /**
     * Adds a user to an event and adds the event to the user's events
     *
     * @param user the user to add to the event
     */
    public void addAttendee(User user) {
        this.attendees.add(user);
        user.getEvents().add(this);
    }
    
    /**
     * Adds an user to {@link #usersThatLikeThisEvent}
     * Adds the event to the user's {@link User#getLikedEvents()}
     * Increases {@link #amountOfLikes by one}
     *
     * @param user the user that likes this event
     */
    public void addUserThatLikesTheEvent(User user) {
        this.usersThatLikeThisEvent.add(user);
        user.getLikedEvents().add(this);
        amountOfLikes++;
    }
    
    /**
     * Removes an user from {@link #usersThatLikeThisEvent}
     * Removes the event from the user's {@link User#getLikedEvents()}
     * Decreases {@link #amountOfLikes by one}
     *
     * @param user the user that dislikes this event
     */
    public void removeUserThatLikesTheEvent(User user) {
        this.usersThatLikeThisEvent.remove(user);
        user.getLikedEvents().remove(this);
        amountOfLikes--;
    }
}
