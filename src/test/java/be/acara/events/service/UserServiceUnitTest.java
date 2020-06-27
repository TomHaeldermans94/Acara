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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Optional;

import static be.acara.events.testutil.EventUtil.firstEvent;
import static be.acara.events.testutil.UserUtil.firstUser;
import static be.acara.events.testutil.UserUtil.secondUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Unit Test")
class UserServiceUnitTest {
    /*******************************
     *         dependencies        *
     *******************************/
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EventService eventService;
    
    /*******************************
     *         Class to test       *
     *******************************/
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, eventRepository, roleRepository, eventService);
    }
    
    
    @Nested
    @DisplayName("Find users")
    class Find {
        @Test
        @DisplayName("Find user by id")
        void findById() {
            Long idToFind = 1L;
            User user = firstUser();
            Mockito.when(userRepository.findById(idToFind)).thenReturn(Optional.of(user));
            
            User answer = userService.findById(idToFind);
            
            assertUser(answer, user);
            verify(userRepository, times(1)).findById(idToFind);
        }
        
        @Test
        @DisplayName("Find user by id - not found")
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
        @DisplayName("Find user by username")
        void findByUsername() {
            String username = "username";
            User user = firstUser();
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            
            User answer = userService.findByUsername(username);
            
            
            assertUser(answer, user);
            assertThat(answer).isInstanceOf(User.class);
        }
    }
    
    @Nested
    @DisplayName("Create users")
    class Create {
        @Test
        @DisplayName("Create user")
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
    }
    
    @Nested
    @DisplayName("Edit users")
    class Edit {
        @Test
        @DisplayName("Edit user")
        void editUser() {
            User firstUser = firstUser();
            User secondUser = secondUser();
            secondUser.setId(firstUser.getId());
            
            when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
            when(userRepository.saveAndFlush(firstUser)).thenReturn(secondUser);
            User answer = userService.editUser(secondUser.getId(), secondUser);
            
            assertUser(answer, secondUser);
            verify(userRepository, times(1)).findById(secondUser.getId());
            verify(userRepository, times(1)).saveAndFlush(firstUser);
        }
        
        @Test
        @DisplayName("Edit user - mismatching id")
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
    }
    
    @Nested
    @DisplayName("User has")
    class Has {
        @Test
        @DisplayName("User has equal id")
        void hasUserId() {
            Authentication auth = mock(Authentication.class);
            Long id = 1L;
            User user = firstUser();
            
            when(auth.getPrincipal()).thenReturn(user);
            
            boolean answer = userService.hasUserId(auth, id);
            
            assertThat(answer).isTrue();
        }
        
        @Test
        @DisplayName("User has equal id - false")
        void hasUserId_isFalse() {
            Authentication auth = mock(Authentication.class);
            Long id = 2L;
            User user = firstUser();
            
            when(auth.getPrincipal()).thenReturn(user);
            
            boolean answer = userService.hasUserId(auth, id);
            
            assertThat(answer).isFalse();
        }
        
        @Test
        @DisplayName("User has equal id - false - other user")
        void hasUserId_withOtherUser() {
            Authentication auth = mock(Authentication.class);
            Long id = 1L;
            AnonymousAuthenticationToken authenticationToken = mock(AnonymousAuthenticationToken.class);
            
            when(auth.getPrincipal()).thenReturn(authenticationToken);
            
            boolean answer = userService.hasUserId(auth, id);
            
            assertThat(answer).isFalse();
        }
    }
    
    @Nested
    @DisplayName("User (dis)likes")
    class Likes {
        @Test
        @DisplayName("User likes an event")
        void likeEvent() {
            User user = firstUser();
            Event event = firstEvent();
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(eventService.findById(any())).thenReturn(event);
            userService.likeEvent(1L, 1L);
            verify(eventRepository, times(1)).saveAndFlush(event);
        }
        
        @Test
        @DisplayName("User dislikes an event")
        void dislikeEvent() {
            User user = firstUser();
            Event event = firstEvent();
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(eventService.findById(any())).thenReturn(event);
            userService.dislikeEvent(1L, 1L);
            verify(eventRepository, times(1)).saveAndFlush(event);
        }
    }
    
    
    private void assertUser(User answer, User providedUser) {
        assertThat(answer).isEqualTo(providedUser);
        assertThat(answer.getId()).isEqualTo(providedUser.getId());
        assertThat(answer.getFirstName()).isEqualTo(providedUser.getFirstName());
        assertThat(answer.getLastName()).isEqualTo(providedUser.getLastName());
        assertThat(answer.getEvents()).isEqualTo(providedUser.getEvents());
        assertThat(answer.getPassword()).isEqualTo(providedUser.getPassword());
        assertThat(answer.getRoles()).isEqualTo(providedUser.getRoles());
        assertThat(answer.getUsername()).isEqualTo(providedUser.getUsername());
        assertThat(answer.getEmail()).isEqualTo(providedUser.getEmail());
    }
}
