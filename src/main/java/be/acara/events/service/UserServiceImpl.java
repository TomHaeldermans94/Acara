package be.acara.events.service;

import be.acara.events.domain.User;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.repository.RoleRepository;
import be.acara.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
    }
    
    /**
     * This is an utility method that calls {@link #loadUserByUsername(String)} and casts it to {@link User}
     * @param username the name of the user
     * @return an user with the specified name
     */
    @Override
    public User findByUsername(String username) {
        return (User) loadUserByUsername(username);
    }

    @Override
    public void save(User user) {
        user.setRoles(Set.of(roleRepository.findRoleByName("ROLE_USER")));
        userRepository.saveAndFlush(user);
    }
    
    @Override
    public User editUser(Long id, User newUser) {
        User oldUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", id)));
        if (!newUser.getId().equals(id)) {
            throw new IdNotFoundException(String.format("Id of user to edit does not match given id. User id = %d, and given id = %d", newUser.getId(), id));
        }
        if(!oldUser.getFirstName().equals(newUser.getFirstName())){
            oldUser.setFirstName(newUser.getFirstName());
        }
        if(!oldUser.getLastName().equals(newUser.getLastName())){
            oldUser.setLastName(newUser.getLastName());
        }
        if(!oldUser.getPassword().equals(newUser.getPassword())){
            oldUser.setPassword(newUser.getPassword());
        }
        return userRepository.saveAndFlush(oldUser);
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
    
    @Override
    public boolean hasUserId(Authentication authentication, Long userId) {
        if (authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getId().equals(userId);
        }
        return false;
    }
}
