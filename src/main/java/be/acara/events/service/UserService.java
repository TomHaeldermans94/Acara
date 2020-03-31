package be.acara.events.service;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.repository.UserRepository;
import be.acara.events.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(mapper::map)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
    }
}
