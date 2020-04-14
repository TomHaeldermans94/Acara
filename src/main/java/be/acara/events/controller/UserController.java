package be.acara.events.controller;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.User;
import be.acara.events.service.UserService;
import be.acara.events.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                userMapper.userToUserDto(
                        userService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> editUser(@PathVariable("id") Long id, @RequestBody @Valid UserDto user) {
        User editedUser = userService.editUser(id, userMapper.userDtoToUser(user));
        return ResponseEntity.ok(userMapper.userToUserDto(editedUser));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable("username") String username){
        boolean check = userService.checkUsername(username);
        return ResponseEntity.ok(check);
    }
}
