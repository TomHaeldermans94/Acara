package be.acara.events.service;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.Role;
import be.acara.events.domain.User;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.repository.RoleRepository;
import be.acara.events.repository.UserRepository;
import be.acara.events.service.mapper.UserMapper;
import be.acara.events.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    
    @Mock
    private UserRepository userRepository;
    private UserService userService;
    @Mock
    private RoleRepository roleRepository;
    
    @BeforeEach
    void setUp() {
        UserMapper userMapper = new UserMapper();
        userService = new UserService(userRepository,roleRepository, userMapper);
    }

    @Test
    void findById() {
        Long idToFind = 1L;
        Mockito.when(userRepository.findById(idToFind)).thenReturn(Optional.of(UserUtil.firstUser()));
    
        UserDto answer = userService.findById(idToFind);
        
        assertUser(UserUtil.map(answer));
        verify(userRepository, times(1)).findById(idToFind);
    }
    
    @Test
    void findById_notFound() {
        Long idToFind = Long.MAX_VALUE;
        Mockito.when(userRepository.findById(idToFind)).thenReturn(Optional.empty());
        
        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> userService.findById(idToFind));
        
        assertThat(thrownException).isNotNull();
        assertThat(thrownException.getTitle()).isEqualTo("User not found");
        assertThat(thrownException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(thrownException.getMessage()).isEqualTo(String.format("User with ID %d not found", idToFind));
        
        verify(userRepository, times(1)).findById(idToFind);
    }
    
    @Test
    void save() {
        User user = UserUtil.firstUser();
        user.setId(null);
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        role.setUsers(Collections.emptySet());
        
        when(userRepository.saveAndFlush(user)).thenReturn(UserUtil.firstUser());
        when(roleRepository.findRoleByName(anyString())).thenReturn(role);
        userService.save(user);
    
        verify(userRepository, times(1)).saveAndFlush(user);
    }
    
    

    private void assertUser(User user) {
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getFirstName()).isNotNull();
        assertThat(user.getLastName()).isNotNull();
        assertThat(user.getEvents()).isNotNull();
        assertThat(user.getPassword()).isNotNull();
        assertThat(user.getRoles()).isNotNull();
        assertThat(user.getUsername()).isNotNull();
    }
}
