package be.acara.events.service;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.repository.UserRepository;
import be.acara.events.service.mapper.EventMapper;
import be.acara.events.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    @Autowired
    public UserService(UserRepository userRepository, EventRepository eventRepository, UserMapper userMapper, EventMapper eventMapper) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::map)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
    }
}
