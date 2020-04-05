package be.acara.events.service;

import be.acara.events.domain.User;
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
    private final UserMapper mapper;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }
    
    public void save(User user) {
        user.setRoles(Set.of(roleRepository.findRoleByName("ROLE_USER")));
        userRepository.saveAndFlush(user);
    }
}
