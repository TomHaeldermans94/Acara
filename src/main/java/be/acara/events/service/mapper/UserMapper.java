package be.acara.events.service.mapper;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.Event;
import be.acara.events.domain.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final EventMapper eventMapper;

    public UserMapper(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public UserDto map(User user){
        Set<EventDto> eventDtos = user.getEvents().stream().map(eventMapper::map).collect(Collectors.toSet());
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .id(user.getId())
                .events(eventDtos)
                .build();
    }

    public User map(UserDto userDto){
        Set<Event> events = userDto.getEvents().stream().map(eventMapper::map).collect(Collectors.toSet());
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .id(userDto.getId())
                .events(events)
                .build();
    }
}
