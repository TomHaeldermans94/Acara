package be.acara.events.controller;

import be.acara.events.controller.dto.CreateOrderDto;
import be.acara.events.controller.dto.EventDto;
import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.Event;
import be.acara.events.domain.Order;
import be.acara.events.service.OrderService;
import be.acara.events.service.mapper.OrderMapper;
import be.acara.events.util.WithMockAdmin;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static be.acara.events.util.EventUtil.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = OrderController.class)
public class OrderControllerTest {
    @MockBean
    private OrderService orderService;
    @MockBean
    private OrderMapper orderMapper;

    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    void createOrder() {
        CreateOrderDto createOrderDto = new CreateOrderDto(1L,1);
        CreateOrder createOrder = new CreateOrder(createOrderDto.getEventId(), createOrderDto.getAmountOfTickets());
        Order order = Mockito.mock(Order.class);
        Mockito.when(orderMapper.orderDtoToOrder(createOrderDto)).thenReturn(createOrder);
        Mockito.when(orderService.create(createOrder)).thenReturn(order);
        Mockito.when(order.getId()).thenReturn(1L);
        RestAssuredMockMvc.given()
                .body(createOrderDto)
                .contentType(ContentType.JSON)
                .when()
                .post("http://localhost/api/orders")
                .then()
                .log().all()
                .statusCode(201)
                .header("Location", String.format("/api/orders/%d", order.getId()));
    }
}
