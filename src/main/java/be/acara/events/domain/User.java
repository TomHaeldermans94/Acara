package be.acara.events.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;
    @Length(min = 2, max = 30)
    private String firstName;
    @Length(min = 2, max = 30)
    private String lastName;
    @ManyToMany(mappedBy = "attendees")
    private Set<Event> events;
    @Length(min = 2, max = 30)
    private String userName;
    @NotBlank
    private String password;

    @PreRemove
    private void removeUsersFromEvents() {
        for (Event event : events) {
            event.getAttendees().remove(this);
        }
    }
}
