package be.acara.events.service;

import be.acara.events.domain.Event;
import be.acara.events.domain.Role;
import be.acara.events.domain.User;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.repository.EventRepository;
import be.acara.events.repository.RoleRepository;
import be.acara.events.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static be.acara.events.util.EventUtil.firstEvent;
import static be.acara.events.util.UserUtil.firstUser;
import static be.acara.events.util.UserUtil.secondUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private RoleRepository roleRepository;

    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, eventRepository, roleRepository);
    }

    @Test
    void findById() {
        Long idToFind = 1L;
        Mockito.when(userRepository.findById(idToFind)).thenReturn(Optional.of(firstUser()));
    
        User answer = userService.findById(idToFind);
        
        assertUser(answer);
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
        User user = firstUser();
        user.setId(null);
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        role.setUsers(Collections.emptySet());
        
        when(userRepository.saveAndFlush(user)).thenReturn(firstUser());
        when(roleRepository.findRoleByName(anyString())).thenReturn(role);
        userService.save(user);
    
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    void editUser() {
        User firstUser = firstUser();
        User secondUser = secondUser();
        secondUser.setId(firstUser.getId());

        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        when(userRepository.saveAndFlush(firstUser)).thenReturn(secondUser);
        User answer = userService.editUser(secondUser.getId(), secondUser);

        assertThat(answer).isNotNull();
        assertThat(answer).isEqualTo(secondUser);
        verify(userRepository, times(1)).findById(secondUser.getId());
        verify(userRepository, times(1)).saveAndFlush(firstUser);
    }

    @Test
    void editUser_withMismatchingId() {
        User firstUser = firstUser();
        User secondUser = secondUser();

        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, () -> userService.editUser(firstUser.getId(), secondUser));

        assertThat(idNotFoundException).isNotNull();
        assertThat(idNotFoundException.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(idNotFoundException.getTitle()).isEqualTo("Cannot process entry");
        assertThat(idNotFoundException.getMessage()).isEqualTo(String.format("Id of user to edit does not match given id. User id = %d, and given id = %d", secondUser.getId(), firstUser.getId()));
        verify(userRepository, times(1)).findById(firstUser.getId());
        verify(userRepository, times(0)).saveAndFlush(secondUser);
    }

    @Test
    void checkIfUserIsPresentInDb() {
        User firstUser = firstUser();
        when(userRepository.findByUsername(firstUser.getUsername())).thenReturn(firstUser);
        Boolean answer = userService.checkUsername(firstUser.getUsername());
        assertTrue(answer);
        verify(userRepository, times(1)).findByUsername(firstUser.getUsername());
    }

    @Test
    void likeEvent() {
        User user = firstUser();
        Event event = firstEvent();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("name");
        when(eventRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(eventRepository.findById(any())).thenReturn(Optional.of(event));


        userService.likeEvent(1L);
        verify(eventRepository,times(1)).saveAndFlush(event);
    }

    @Test
    void dislikeEvent() {
        User user = firstUser();
        Event event = firstEvent();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("name");
        when(eventRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(eventRepository.findById(any())).thenReturn(Optional.of(event));


        userService.dislikeEvent(1L);
        verify(eventRepository,times(1)).saveAndFlush(event);
    }

    @Test
    void findEventById() {
        Long idToFind = 1L;
        Mockito.when(eventRepository.findById(idToFind)).thenReturn(Optional.of(firstEvent()));
        Event answer = userService.getEventById(idToFind);
        verify(eventRepository, times(1)).findById(idToFind);
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
