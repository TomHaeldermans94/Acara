package be.acara.events.service;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.repository.RoleRepository;
import be.acara.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RoleRepository roleRepository;
    private final EventService eventService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EventRepository eventRepository, RoleRepository roleRepository, @Lazy EventService eventService) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.roleRepository = roleRepository;
        this.eventService = eventService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByUsername(String username) {
        return (User) loadUserByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(User user) {
        user.setRoles(Set.of(roleRepository.findRoleByName("ROLE_USER")));
        userRepository.saveAndFlush(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User editUser(Long id, User newUser) {
        User oldUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
        if (!newUser.getId().equals(id)) {
            throw new IdNotFoundException(String.format("Id of user to edit does not match given id. User id = %d, and given id = %d", newUser.getId(), id));
        }
        if (!oldUser.getFirstName().equals(newUser.getFirstName())) {
            oldUser.setFirstName(newUser.getFirstName());
        }
        if (!oldUser.getLastName().equals(newUser.getLastName())) {
            oldUser.setLastName(newUser.getLastName());
        }
        if (!oldUser.getPassword().equals(newUser.getPassword())) {
            oldUser.setPassword(newUser.getPassword());
        }
        if (!oldUser.getEmail().equals(newUser.getEmail())) {
            oldUser.setEmail(newUser.getEmail());
        }
        return userRepository.saveAndFlush(oldUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUserId(Authentication authentication, Long userId) {
        if (authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getId().equals(userId);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void likeEvent(Long userId, Long eventId) {
        User user = findById(userId);
        Event event = eventService.findById(eventId);
        event.addUserThatLikesTheEvent(user);
        eventRepository.saveAndFlush(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dislikeEvent(Long userId, Long eventId) {
        User user = findById(userId);
        Event event = eventService.findById(eventId);
        event.removeUserThatLikesTheEvent(user);
        eventRepository.saveAndFlush(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(userName)) {
            return null;
        }
        return findByUsername(userName);
    }
}
