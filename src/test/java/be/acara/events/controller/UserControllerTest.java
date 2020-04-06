package be.acara.events.controller;

import be.acara.events.controller.dto.UserDto;
import be.acara.events.exceptions.ControllerExceptionAdvice;
import be.acara.events.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static be.acara.events.util.UserUtil.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    @InjectMocks
    private ControllerExceptionAdvice controllerExceptionAdvice;


    @BeforeEach
    void setUp() {
        standaloneSetup(userController, controllerExceptionAdvice, springSecurity((request, response, chain) -> chain.doFilter(request, response)));
    }

    @Test
    void findById() {
        Long id = 1L;
        UserDto userDto = map(firstUser());
        when(userService.findById(id)).thenReturn(userDto);

        UserDto answer = given()
                .when()
                .get(RESOURCE_URL + "/{id}", id)
                .then()
                .log().ifError()
                .status(HttpStatus.OK)
                .extract().as(UserDto.class);

        assertUser(answer, userDto);
        verifyOnce().findById(id);
    }

    private void assertUser(UserDto response, UserDto expected) {
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expected);
    }

    private UserService verifyOnce() {
        return verify(userService, times(1));
    }

}
