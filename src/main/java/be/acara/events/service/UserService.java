package be.acara.events.service;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.User;

public interface UserService {
    UserDto findById(Long id);
    
    void save(User user);
}
