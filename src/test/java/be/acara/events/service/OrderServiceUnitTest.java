package be.acara.events.service;

import be.acara.events.controller.dto.CreateOrderDto;
import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.Event;
import be.acara.events.domain.Order;
import be.acara.events.domain.User;
import be.acara.events.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;

import static be.acara.events.util.EventUtil.firstEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private EventService eventService;

    @Mock
    private Event event;


    private OrderService orderService;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, eventService, userService);
    }


    @Test
    void createOrder() {
        CreateOrder createOrder = new CreateOrder(1L, 1);
        Event event = eventService.findById(createOrder.getEventId());
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(userName);

        Order order = orderRepository.saveAndFlush(
                Order.builder()
                        .event(event)
                        .user(user)
                        .amountOfTickets(createOrder.getAmountOfTickets())
                        .total(event.getPrice().multiply(new BigDecimal(createOrder.getAmountOfTickets())))
                        .build());
        eventService.addAttendee(event, user);

        order.setId(null);


        when(orderRepository.saveAndFlush(order)).thenReturn(order);
        Order answer = orderService.create(createOrder);

        assertThat(answer).isEqualTo(order);
        verify(orderRepository, times(1)).saveAndFlush(order);
    }
}
