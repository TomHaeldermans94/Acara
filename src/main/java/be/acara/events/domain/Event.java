package be.acara.events.domain;

import be.acara.events.domain.converter.CategoryConverter;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
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
    private Set<User> attendees;
    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Objects.equals(getId(), event.getId()) &&
                Objects.equals(getEventDate(), event.getEventDate()) &&
                Objects.equals(getName(), event.getName()) &&
                Objects.equals(getDescription(), event.getDescription()) &&
                Arrays.equals(getImage(), event.getImage()) &&
                Objects.equals(getLocation(), event.getLocation()) &&
                getCategory() == event.getCategory() &&
                Objects.equals(getAttendees(), event.getAttendees()) &&
                Objects.equals(getPrice(), event.getPrice());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getEventDate(), getName(), getDescription(), getLocation(), getCategory(), getAttendees(), getPrice());
        result = 31 * result + Arrays.hashCode(getImage());
        return result;
    }
}
