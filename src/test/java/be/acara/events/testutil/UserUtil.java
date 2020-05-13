package be.acara.events.testutil;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.service.mapper.UserMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static be.acara.events.testutil.EventUtil.createSetOfEventsOfSize3;

public class UserUtil {
    public static final String RESOURCE_URL = "http://localhost/api/users";
    
    public static User firstUser() {
        Set<Event> events = createSetOfEventsOfSize3();
        return User.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .username("username")
                .password("password")
                .email("email")
                .events(events)
                .likedEvents(new HashSet<>())
                .roles(Collections.emptySet())
                .build();
    }
    
    public static User secondUser() {
        Set<Event> events = createSetOfEventsOfSize3();
        return User.builder()
                .id(2L)
                .firstName("firstName2")
                .lastName("lastName2")
                .username("username2")
                .password("password2")
                .email("email2")
                .events(events)
                .likedEvents(Collections.emptySet())
                .roles(Collections.emptySet())
                .build();
    }

    public static User thirdUser() {
        Set<Event> events = createSetOfEventsOfSize3();
        return User.builder()
                .id(3L)
                .firstName("firstName3")
                .lastName("lastName3")
                .username("username3")
                .password("password3")
                .email("email3")
                .events(events)
                .likedEvents(Collections.emptySet())
                .roles(Collections.emptySet())
                .build();
    }

    public static UserDto map(User user) {
        return UserMapper.INSTANCE.userToUserDto(user);
    }
}
