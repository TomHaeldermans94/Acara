package be.acara.events.controller;

import be.acara.events.controller.dto.LikeEventDto;
import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.User;
import be.acara.events.service.UserService;
import be.acara.events.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, UserMapper userMapper) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }
    
    @PostMapping("/sign-up")
    public void signUp(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userService.save(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userServiceImpl.hasUserId(authentication, #id) or hasRole('ADMIN')")
    public ResponseEntity<UserDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                userMapper.userToUserDto(
                        userService.findById(id)));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@userServiceImpl.hasUserId(authentication, #id) or hasRole('ADMIN')")
    public ResponseEntity<UserDto> editUser(@PathVariable("id") Long id, @RequestBody @Valid UserDto user) {
        User editedUser = userService.editUser(id, userMapper.userDtoToUser(user));
        return ResponseEntity.ok(userMapper.userToUserDto(editedUser));
    }

    @PostMapping("/{userId}/likes")
    public ResponseEntity<Void> likeEvent(@PathVariable("userId") Long userId, @RequestBody LikeEventDto likeEventDto) {
        userService.likeEvent(userId, likeEventDto.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/likes/{eventId}")
    public ResponseEntity<Void> dislikeEvent(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        userService.dislikeEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}
