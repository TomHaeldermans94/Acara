package be.acara.events.controller;

import be.acara.events.controller.dto.CreateOrderDto;
import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.Order;
import be.acara.events.service.OrderService;
import be.acara.events.service.UserService;
import be.acara.events.service.mapper.OrderMapper;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = OrderController.class)
public class OrderControllerTest {
    @MockBean
    private OrderService orderService;
    @MockBean
    private OrderMapper orderMapper;

    @MockBean
    private UserService userDetailsService;
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
