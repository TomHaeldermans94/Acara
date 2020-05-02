package be.acara.events.service;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;

public interface UserService {
    User findById(Long id);

    User findByUsername(String username);
    
    void save(User user);
    
    User editUser(Long id, User user);
    
    Boolean checkUsername(String username);

    void likeEvent(Long id);

    void dislikeEvent(Long id);

    boolean doesUserLikeThisEvent(Long id);

    User getCurrentUser();

    Event getEventById(Long id);
}
