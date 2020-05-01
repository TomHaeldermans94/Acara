package be.acara.events.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
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
    @EqualsAndHashCode.Exclude
    private Set<Event> events;
    @ManyToMany(mappedBy = "usersThatLikeThisEvent")
    @EqualsAndHashCode.Exclude
    private Set<Event> likedEvents;
    @Length(min = 2, max = 30)
    @Column(unique = true)
    private String username;
    @NotBlank
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private Set<Role> roles;
    @Email
    private String email;
}
