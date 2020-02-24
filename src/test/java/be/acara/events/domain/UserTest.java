package be.acara.events.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    private User user;
    
    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("first")
                .lastName("last")
                .events(new HashSet<>())
                .build();
    }
    
    @Test
    void createUser() {
        user = new User();
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getEvents()).isNull();
    }
    
    @Test
    void getId() {
        assertThat(user.getId()).isEqualTo(1L);
    }
    
    @Test
    void getFirstName() {
        assertThat(user.getFirstName()).isEqualTo("first");
    }
    
    @Test
    void getLastName() {
        assertThat(user.getLastName()).isEqualTo("last");
    }
    
    @Test
    void getEvents() {
        assertThat(user.getEvents()).isNotNull();
        assertThat(user.getEvents()).isEmpty();
    }
    
    @Test
    void setId() {
        Long id = 10L;
        user.setId(id);
        assertThat(user.getId()).isEqualTo(id);
    }
    
    @Test
    void setFirstName() {
        String firstName = "Not first name";
        user.setFirstName(firstName);
        assertThat(user.getFirstName()).isEqualTo(firstName);
    }
    
    @Test
    void setLastName() {
        String lastName = "Totally my last name";
        user.setLastName(lastName);
        assertThat(user.getLastName()).isEqualTo(lastName);
    }
    
    @Test
    void setEvents() {
        Event mock = Mockito.mock(Event.class);
        Set<Event> newListOfEvents = Set.of(mock);
        user.setEvents(newListOfEvents);
        assertThat(user.getEvents()).isNotNull();
        assertThat(user.getEvents()).hasSize(1);
        assertThat(user.getEvents()).contains(mock);
    }
}
