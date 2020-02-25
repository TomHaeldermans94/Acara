package be.acara.events.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.map;

class EventTest {
    private Event event;
    private LocalDateTime dateTime;
    
    @BeforeEach
    void setUp() {
        dateTime = LocalDateTime.now().plusDays(2);
        User mock = Mockito.mock(User.class);
        event = Event.builder()
                .id(1L)
                .description("This is a description")
                .eventDate(dateTime)
                .name("Event!")
                .image(new byte[0])
                .attendees(Set.of(mock))
                .location("location")
                .price(new BigDecimal("20.02"))
                .build();
    }
    
    @Test
    void eventCreator() {
        event = new Event();
        assertThat(event).isNotNull();
    }
    
    @Test
    void getId() {
        assertThat(event.getId()).isEqualTo(1L);
    }
    
    @Test
    void getEventDate() {
        assertThat(event.getEventDate()).isEqualTo(dateTime);
    }
    
    @Test
    void getName() {
        assertThat(event.getName()).isEqualTo("Event!");
    }
    
    @Test
    void getDescription() {
        assertThat(event.getDescription()).isEqualTo("This is a description");
    }
    
    @Test
    void getImage() {
        assertThat(event.getImage()).isEqualTo(new byte[0]);
    }
    
    @Test
    void setId() {
        Long id = 10L;
        event.setId(id);
        assertThat(event.getId()).isEqualTo(id);
    }
    
    @Test
    void setEventDate() {
        LocalDateTime date = LocalDateTime.now().plusDays(7);
        event.setEventDate(date);
        assertThat(event.getEventDate()).isEqualTo(date);
    }
    
    @Test
    void setName() {
        String name = "New name";
        event.setName(name);
        assertThat(event.getName()).isEqualTo(name);
    }
    
    @Test
    void setDescription() {
        String description = "Description";
        event.setDescription(description);
        assertThat(event.getDescription()).isEqualTo(description);
    }
    
    @Test
    void setImage() {
        byte[] image = new byte[10];
        event.setImage(image);
        assertThat(event.getImage()).isEqualTo(image);
    }
    
    @Test
    void getAttendees() {
        assertThat(event.getAttendees()).isNotNull();
        assertThat(event.getAttendees()).hasSize(1);
    }
    
    @Test
    void getPrice() {
        assertThat(event.getPrice()).isEqualTo(new BigDecimal("20.02"));
    }
    
    @Test
    void setAttendees() {
        Set<User> users = new HashSet<>();
        for (int i = 0; i < 2; i++) {
            User mock = Mockito.mock(User.class);
            users.add(mock);
        }
        event.setAttendees(users);
        assertThat(event.getAttendees()).hasSize(2);
    }
    
    @Test
    void setPrice() {
        BigDecimal price = new BigDecimal("11.11");
        event.setPrice(price);
        assertThat(event.getPrice()).isEqualTo(price);
    }
    
    @Test
    void getLocation() {
        assertThat(event.getLocation()).isEqualTo("location");
    }
    
    @Test
    void setLocation() {
        String newLocation = "New location";
        event.setLocation(newLocation);
        assertThat(event.getLocation()).isEqualTo(newLocation);
    }
}
