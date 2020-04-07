package be.acara.events.util;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import be.acara.events.service.mapper.UserMapper;

import java.util.Set;

import static be.acara.events.util.EventUtil.createSetOfEventsOfSize3;

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
                .events(events)
                .build();
    }

    public static UserDto firstUserDto() {
        return UserDto.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .username("username")
                .build();
    }

    public static UserDto map(User user) {
        return new UserMapper().map(user);
    }
    
    public static User map(UserDto user) {
        return new UserMapper().map(user);
    }
}
