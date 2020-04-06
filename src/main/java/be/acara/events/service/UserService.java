package be.acara.events.service;

import be.acara.events.domain.User;
import be.acara.events.repository.RoleRepository;
import be.acara.events.controller.dto.UserDto;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.repository.UserRepository;
import be.acara.events.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::map)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
    }
    
    public void save(User user) {
        user.setRoles(Set.of(roleRepository.findRoleByName("ROLE_USER")));
        userRepository.saveAndFlush(user);
    }
}
