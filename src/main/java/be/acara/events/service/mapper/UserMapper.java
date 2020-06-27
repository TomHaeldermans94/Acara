package be.acara.events.service.mapper;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserDto userToUserDto(User user);
    User userDtoToUser(UserDto user);
}
