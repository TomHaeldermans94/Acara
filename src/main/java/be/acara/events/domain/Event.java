package be.acara.events.domain;

import be.acara.events.domain.converter.CategoryConverter;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int amountOfLikes;
    @FutureOrPresent
    private LocalDateTime eventDate;
    @Length(min = 2, max = 40)
    private String name;
    @Length(max = 2048)
    private String description;
    @Lob
    private byte[] image;
    @Length(min = 2, max = 40)
    private String location;
    @NotNull
    @Convert(converter = CategoryConverter.class)
    private Category category;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REFRESH})
    @JoinTable(name = "EVENT_ATTENDEES",
            joinColumns = @JoinColumn(name = "EVENT_ID", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ATTENDEES_ID", referencedColumnName = "id"))
    @EqualsAndHashCode.Exclude
    private Set<User> attendees;
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE,CascadeType.REFRESH})
    @JoinTable(name = "EVENT_USERS_THAT_LIKE_THIS_EVENT",
            joinColumns = @JoinColumn(name = "EVENT_ID", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "USERS_THAT_LIKE_THIS_EVENT_ID", referencedColumnName = "id"))
    @EqualsAndHashCode.Exclude
    private Set<User> usersThatLikeThisEvent;
    private BigDecimal price;

    private String youtubeId;

    public void addAttendee(User user) {
        this.attendees.add(user);
        user.getEvents().add(this);
    }

    public void addUserThatLikesTheEvent(User user) {
        this.usersThatLikeThisEvent.add(user);
        user.getLikedEvents().add(this);
        amountOfLikes++;
    }

    public void removeUserThatLikesTheEvent(User user) {
        this.usersThatLikeThisEvent.remove(user);
        user.getLikedEvents().remove(this);
        amountOfLikes--;
    }
}
