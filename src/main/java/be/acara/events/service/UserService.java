package be.acara.events.service;

import be.acara.events.domain.User;

public interface UserService {
    User findById(Long id);

    User findByUsername(String username);
    
    void save(User user);
    
    User editUser(Long id, User user);
    
    Boolean checkUsername(String username);

    void likeEvent(Long userId, Long eventId);

    void dislikeEvent(Long userId, Long eventId);

    boolean doesUserLikeThisEvent(Long userId, Long eventId);

    User getCurrenUser();
}
