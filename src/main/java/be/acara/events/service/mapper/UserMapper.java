package be.acara.events.service.mapper;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.User;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserMapper {

    public UserDto map(User user){
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .password(user.getPassword())
                .id(user.getId())
                .build();
    }

    public User map(UserDto userDto){
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .id(userDto.getId())
                .events(Collections.emptySet())
                .password("")
                .roles(Collections.emptySet())
                .username(userDto.getUsername())
                .build();
    }
}
