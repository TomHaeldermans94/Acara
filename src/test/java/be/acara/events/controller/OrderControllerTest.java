package be.acara.events.controller;

import be.acara.events.controller.dto.CreateOrderDto;
import be.acara.events.controller.dto.CreateOrderDtoList;
import be.acara.events.controller.dto.OrderDto;
import be.acara.events.controller.dto.OrderDtoList;
import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.Order;
import be.acara.events.service.OrderService;
import be.acara.events.service.UserService;
import be.acara.events.service.mapper.OrderMapper;
import be.acara.events.testutil.OrderUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        mockMvc(mockMvc);
    }

    @Test
    @WithMockUser
    void createOrder() {
        CreateOrderDto createOrderDto = new CreateOrderDto(1L,1);
        CreateOrder createOrder = new CreateOrder(createOrderDto.getEventId(), createOrderDto.getAmountOfTickets());
        Order order = Mockito.mock(Order.class);
        when(orderMapper.orderDtoToOrder(createOrderDto)).thenReturn(createOrder);
        when(orderService.create(createOrder)).thenReturn(order);
        when(order.getId()).thenReturn(1L);
        given()
                .body(createOrderDto)
                .contentType(ContentType.JSON)
                .when()
                .post("http://localhost/api/orders")
                .then()
                .log().all()
                .statusCode(201)
                .header("Location", String.format("/api/orders/%d", order.getId()));
    }
    
    @Test
    @WithMockUser
    void createOrder_batch() {
        CreateOrderDtoList createOrderDtoList = new CreateOrderDtoList(Set.of(new CreateOrderDto(1L, 1)));
        CreateOrderList createOrderList = new CreateOrderList(Set.of(OrderUtil.createOrder()));
    
        when(orderMapper.createOrderDtoListToCreateOrderList(createOrderDtoList)).thenReturn(createOrderList);
        doNothing().when(orderService).createAll(createOrderList);
        
        given()
                .body(createOrderList)
                .contentType(ContentType.JSON)
                .when()
                .post("http://localhost/api/orders/batch")
                .then()
                .log().all()
                .statusCode(201)
                .header("Location", "/api/orders/");
    }
    
    @Test
    @WithMockUser
    void removeOrder() {
        Long id = 1L;
        doNothing().when(orderService).remove(id);
        
        given()
                .when()
                .delete("http://localhost/api/orders/{id}", id)
                .then()
                .log().ifError()
                .status(HttpStatus.NO_CONTENT);
        
        verify(orderService, times(1)).remove(id);
    }
    
    @Test
    @WithMockUser
    void getAllOrders() {
        Order order = OrderUtil.order();
        OrderDto orderDto = OrderUtil.orderDto();
        PageImpl<Order> orders = new PageImpl<>(List.of(order));
        OrderDtoList orderList = new OrderDtoList(List.of(orderDto));
        
        when(orderService.getAllOrders(any())).thenReturn(orders);
        when(orderMapper.pageToOrderList(orders)).thenReturn(orderList);
    
        OrderDtoList answer = given()
                .when()
                .get("http://localhost/api/orders")
                .then()
                .log().ifError()
                .status(HttpStatus.OK)
                .contentType(ContentType.JSON)
                .extract().as(OrderDtoList.class);
        
        assertThat(answer).isEqualTo(orderList);
    }
}
