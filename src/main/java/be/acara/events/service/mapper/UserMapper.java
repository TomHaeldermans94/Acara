package be.acara.events.service.mapper;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserDto userToUserDto(User user);
    User userDtoToUser(UserDto user);

    /*public UserDto map(User user){
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
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
    }*/
}
