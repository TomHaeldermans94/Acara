package be.acara.events.controller;

import be.acara.events.controller.dto.ApiError;
import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.User;
import be.acara.events.exceptions.UserNotFoundException;
import be.acara.events.service.UserService;
import be.acara.events.service.mapper.UserMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static be.acara.events.util.UserUtil.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @SpyBean
    private UserMapper userMapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    void findById() {
        Long id = 1L;
        User user = firstUser();
        when(userService.findById(id)).thenReturn(user);

        UserDto answer = given()
                .when()
                .get(RESOURCE_URL + "/{id}", id)
                .then()
                .log().ifError()
                .status(HttpStatus.OK)
                .extract().as(UserDto.class);

        assertUser(answer, map(user));
        verifyOnce().findById(id);
    }
    
    @Test
    @WithMockUser
    void findById_notFound() {
        Long id = Long.MAX_VALUE;
        UserNotFoundException userNotFoundException = new UserNotFoundException(String.format("User with ID %d not found", id));
        when(userService.findById(anyLong())).thenThrow(userNotFoundException);
    
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

    private void assertUser(UserDto response, UserDto expected) {
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expected);
    }

    private UserService verifyOnce() {
        return verify(userService, times(1));
    }

}
