package be.acara.events.service;

import be.acara.events.domain.User;

public interface UserService {
    User findById(Long id);
    
    void save(User user);
    
    User editUser(Long id, User user);
    
    Boolean checkUsername(String username);
}
