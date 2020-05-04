package be.acara.events.controller;

import be.acara.events.controller.dto.ApiError;
import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.User;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.security.MethodSecurityConfigurer;
import be.acara.events.service.UserService;
import be.acara.events.service.mapper.UserMapper;
import be.acara.events.testutil.WithMockAdmin;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static be.acara.events.testutil.UserUtil.*;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
@Import(MethodSecurityConfigurer.class)
public class UserControllerTest {
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private UserMapper userMapper;
    @MockBean(name = "userServiceImpl")
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }
    
    @Test
    @WithMockAdmin
    void findById() {
        Long id = 1L;
        User user = firstUser();
        UserDto userDto = map(firstUser());
        when(userService.findById(id)).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);
        when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        when(userService.hasUserId(any(), any())).thenReturn(true);
        
        UserDto answer = given()
                .when()
                .get(RESOURCE_URL + "/{id}", id)
                .then()
                .log().ifError()
                .status(HttpStatus.OK)
                .extract().as(UserDto.class);
        
        assertUser(answer, UserMapper.INSTANCE.userToUserDto(user));
        verifyOnce().findById(id);
    }
    
    @Test
    @WithMockAdmin
    void findById_notFound() {
        Long id = Long.MAX_VALUE;
        UserNotFoundException userNotFoundException = new UserNotFoundException(String.format("User with ID %d not found", id));
        when(userService.findById(anyLong())).thenThrow(userNotFoundException);
        when(userService.hasUserId(any(), any())).thenReturn(true);
        
        ApiError exception = given()
                .when()
                .get(RESOURCE_URL + "/{id}", id)
                .then()
                .log().ifError()
                .status(HttpStatus.NOT_FOUND)
                .extract()
                .as(ApiError.class);
        
        assertThat(exception.getStatus()).isEqualTo(userNotFoundException.getStatus().getReasonPhrase());
        assertThat(exception.getMessage()).isEqualTo(userNotFoundException.getMessage());
        assertThat(exception.getTitle()).isEqualTo(userNotFoundException.getTitle());
    }
    
    @Test
    @WithMockUser
    void ownProfile_findById() {
        User user = firstUser();
        UserDto userDto = map(firstUser());
        when(userService.findById(user.getId())).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);
        when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        when(userService.hasUserId(any(), any())).thenReturn(true);
        
        UserDto answer = given()
                .when()
                .get(RESOURCE_URL + "/{id}", user.getId())
                .then()
                .log().ifError()
                .status(HttpStatus.OK)
                .extract().as(UserDto.class);
        
        assertUser(answer, UserMapper.INSTANCE.userToUserDto(user));
        verifyOnce().findById(user.getId());
    }
    
    @Test
    @WithMockUser
    void otherProfile_findById() {
        Long id = Long.MAX_VALUE;
    
        given()
                .when()
                .get(RESOURCE_URL + "/{id}", id)
                .then()
                .log().ifError()
                .status(HttpStatus.FORBIDDEN)
                .body(equalTo("Access is denied"));
    }
    
    @Test
    @WithMockAdmin
    void editUser() {
        User user = firstUser();
        UserDto userDto = map(user);
        when(userService.editUser(user.getId(), user)).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);
        when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        when(userService.hasUserId(any(), any())).thenReturn(true);
        
        UserDto answer = given()
                .body(user)
                .contentType(JSON)
                .when()
                .put(RESOURCE_URL + "/{id}", user.getId())
                .then()
                .log().ifError()
                .status(HttpStatus.OK)
                .extract().as(UserDto.class);
        
        assertUser(answer, map(user));
        verifyOnce().editUser(firstUser().getId(), user);
    }
    
    private void assertUser(UserDto response, UserDto expected) {
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expected);
    }
    
    private UserService verifyOnce() {
        return verify(userService, times(1));
    }
    
}
