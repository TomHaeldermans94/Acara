package be.acara.events.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

/**
 * A role class to allow permissions for an user
 */
@Entity
@Data
@NoArgsConstructor
public class Role {
    /**
     * The id of this role
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The name of the role
     */
    private String name;
    /**
     * The users that have this role
     */
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}

