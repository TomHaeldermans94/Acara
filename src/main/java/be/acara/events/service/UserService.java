package be.acara.events.service;

import be.acara.events.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    /**
     * Finds the user given an id
     *
     * @param id the id of the user to find
     * @return the user with the given id
     */
    User findById(Long id);

    /**
     * This is an utility method that calls {@link #loadUserByUsername(String)} and casts it to {@link User}
     * @param username the name of the user
     * @return an user with the specified name
     */
    User findByUsername(String username);

    /**
     * Save the given user to the database
     * @param user the user to save
     */
    void save(User user);

    /**
     * Edits the details of the user
     * @param id the id of the user to be edited
     * @param user the new body of the user
     * @return the user with the changes applied
     */
    User editUser(Long id, User user);

    /**
     * Checks whether the authentication and the given id are matching
     * @param authentication the authentication object containing the current user's session
     * @param userId the id to check against
     * @return true if the passed id and authentication details id match
     */
    boolean hasUserId(Authentication authentication, Long userId);

    /**
     * Adds the given user to the given event's list of users that like this event
     * @param userId the user who likes the event
     * @param eventId the event the user likes
     */
    void likeEvent(Long userId, Long eventId);

    /**
     * Removes the given user from the given event's list of users that like this event
     * @param userId the user who likes the event
     * @param eventId the event the user likes
     */
    void dislikeEvent(Long userId, Long eventId);

    /**
     * A helper method to retrieve the current authorization from the SecurityContext and cast it to {@link User}
     * @return null if it's an anonymoustoken, otherwise return the logged in User
     */
    User getCurrentUser();
}
