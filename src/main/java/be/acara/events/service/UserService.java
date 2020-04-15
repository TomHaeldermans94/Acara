package be.acara.events.service;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.User;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.repository.RoleRepository;
import be.acara.events.repository.UserRepository;
import be.acara.events.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.eventRepository = eventRepository;
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

    public UserDto editUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
        if (!userDto.getId().equals(user.getId())) {
            throw new IdNotFoundException(String.format("Id of user to edit does not match given id. User id = %d, and given id = %d", userDto.getId(), id)
            );
        }
        if(!userDto.getFirstName().equals(user.getFirstName())){
            user.setFirstName(userDto.getFirstName());
        }
        if(!userDto.getLastName().equals(user.getLastName())){
            user.setLastName(userDto.getLastName());
        }
        if(!userDto.getUsername().equals(user.getUsername())){
            user.setUsername(userDto.getUsername());
        }
        if(!userDto.getPassword().equals(user.getPassword())){
            user.setPassword(userDto.getPassword());
        }
        userRepository.saveAndFlush(user);
        return userDto;
    }

    public Boolean checkUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }
}
