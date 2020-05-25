package be.acara.events.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The user class for this service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User implements UserDetails {
    /**
     * The id of this user
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The first name of this user
     */
    @Length(min = 2, max = 30)
    private String firstName;
    /**
     * The last name of this user
     */
    @Length(min = 2, max = 30)
    private String lastName;
    /**
     * The events this user has subscribed to
     */
    @ManyToMany(mappedBy = "attendees")
    @EqualsAndHashCode.Exclude
    private Set<Event> events;
    /**
     * The events this user has liked
     */
    @ManyToMany(mappedBy = "usersThatLikeThisEvent")
    @EqualsAndHashCode.Exclude
    private Set<Event> likedEvents;
    /**
     * The username of this user
     */
    @Length(min = 2, max = 30)
    @Column(unique = true)
    private String username;
    /**
     * The password of this user
     */
    @NotBlank
    private String password;
    /**
     * The roles of this user
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private Set<Role> roles;
    /**
     * The e-mail of this user
     */
    @Email
    private String email;
    
    /**
     * Returns {@link #roles} in the form of {@link SimpleGrantedAuthority}
     *
     * @return a collection of GrantedAuthorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toUnmodifiableSet());
    }
    
    /**
     * If the account is non-expired
     *
     * @return true if non-expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    /**
     * If the account is non-locked
     *
     * @return true if non-locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    /**
     * If the credentials aren't expired
     *
     * @return true if they aren't expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    /**
     * If the account is enabled
     *
     * @return if the account is enabled
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
