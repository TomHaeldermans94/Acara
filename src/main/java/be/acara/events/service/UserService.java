package be.acara.events.service;

import be.acara.events.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findById(Long id);

    User findByUsername(String username);
    
    void save(User user);
    
    User editUser(Long id, User user);
    
    boolean hasUserId(Authentication authentication, Long userId);
}
