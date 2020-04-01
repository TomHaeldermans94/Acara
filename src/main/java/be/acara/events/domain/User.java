package be.acara.events.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Length(min = 2, max = 30)
    private String firstName;
    @Length(min = 2, max = 30)
    private String lastName;
    @ManyToMany(mappedBy = "attendees")
    private List<Event> events;
    @Length(min = 2, max = 30)
    private String userName;
    @NotBlank
    private String password;

    @PreRemove
    private void removeUsersFromEvents() {
        events.forEach(event -> event.getAttendees().remove(this));
    }
}
