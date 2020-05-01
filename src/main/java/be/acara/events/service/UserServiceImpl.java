package be.acara.events.service;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.repository.RoleRepository;
import be.acara.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EventRepository eventRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void save(User user) {
        user.setRoles(Set.of(roleRepository.findRoleByName("ROLE_USER")));
        userRepository.saveAndFlush(user);
    }
    
    @Override
    public User editUser(Long id, User newUser) {
        User oldUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
        if (!newUser.getId().equals(id)) {
            throw new IdNotFoundException(String.format("Id of user to edit does not match given id. User id = %d, and given id = %d", newUser.getId(), id));
        }
        if(!oldUser.getFirstName().equals(newUser.getFirstName())){
            oldUser.setFirstName(newUser.getFirstName());
        }
        if(!oldUser.getLastName().equals(newUser.getLastName())){
            oldUser.setLastName(newUser.getLastName());
        }
        if(!oldUser.getPassword().equals(newUser.getPassword())){
            oldUser.setPassword(newUser.getPassword());
        }
        return userRepository.saveAndFlush(oldUser);
    }
    
    @Override
    public Boolean checkUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public void likeEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(String.format("Event with ID %d not found", id));
        }
        User user = getCurrentUser();
        Event event = getEventById(id);
        event.addUserThatLikesTheEvent(user);
        eventRepository.saveAndFlush(event);
    }

    @Override
    public void dislikeEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(String.format("Event with ID %d not found", id));
        }
        User user = getCurrentUser();
        Event event = getEventById(id);
        event.removeUserThatLikesTheEvent(user);
        eventRepository.saveAndFlush(event);
    }

    @Override
    public boolean doesUserLikeThisEvent(Long id) {
        User user = getCurrentUser();
        Event event = getEventById(id);
        return user.getLikedEvents().contains(event);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with ID %d not found", id)));
    }

}
