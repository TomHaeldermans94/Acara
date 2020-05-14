package be.acara.events.service;

import be.acara.events.domain.*;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.OrderNotFoundException;
import be.acara.events.repository.OrderRepository;
import be.acara.events.testutil.OrderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static be.acara.events.testutil.EventUtil.firstEvent;
import static be.acara.events.testutil.UserUtil.firstUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {
    
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;
    @Mock
    private SecurityContext securityContext;
    
    private OrderService orderService;
    
    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, eventService, userService);
    }
    
    @Test
    void findById() {
        Long id = 1L;
        Order order = mock(Order.class);
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
    
        Order answer = orderService.findById(id);
        
        assertThat(answer).isEqualTo(order);
        verify(orderRepository, times(1)).findById(id);
    }
    
    @Test
    void findById_notFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(OrderNotFoundException.class, () -> orderService.findById(Long.MAX_VALUE));
    
        verify(orderRepository, times(1)).findById(Long.MAX_VALUE);
    }
    
    @Test
    void create() {
        User user = firstUser();
        Event event = firstEvent();
        CreateOrder createOrder = OrderUtil.createOrder();
        Order order = OrderUtil.order();
        setAuthenticationMocks(user);
        
        when(eventService.findById(anyLong())).thenReturn(event);
        when(orderRepository.saveAndFlush(any())).thenReturn(order);
    
        Order answer = orderService.create(createOrder);
        
        assertThat(answer.getEvent().getId()).isEqualTo(createOrder.getEventId());
        assertThat(answer.getAmountOfTickets()).isEqualTo(createOrder.getAmountOfTickets());
    
        verify(orderRepository, times(1)).saveAndFlush(any());
        verify(eventService, times(1)).findById(anyLong());
    }
    
    @Test
    void create_batch() {
        User user = firstUser();
        Event event = firstEvent();
        CreateOrder createOrder = OrderUtil.createOrder();
        CreateOrderList createOrderList = new CreateOrderList(Set.of(createOrder));
        setAuthenticationMocks(user);
    
        when(eventService.findById(anyLong())).thenReturn(event);
        when(orderRepository.saveAll(any())).thenReturn(null);
    
        orderService.createAll(createOrderList);
    
        verify(orderRepository, times(1)).saveAll(any());
        verify(eventService, times(1)).findById(anyLong());
    }
    
    @Test
    void edit() {
        Order order = OrderUtil.order();
        when(orderRepository.saveAndFlush(any())).thenReturn(order);
        
        orderService.edit(order.getId(), order);
    
        verify(orderRepository, times(1)).saveAndFlush(any());
    }
    
    @Test
    void edit_wrongId() {
        Order order = OrderUtil.order();
        
        assertThrows(IdNotFoundException.class, () -> orderService.edit(2L, order));
        
        verify(orderRepository, times(0)).saveAndFlush(any());
    }
    
    @Test
    void remove() {
        Long id = 1L;
        when(orderRepository.existsById(id)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(id);
        
        orderService.remove(id);
    
        verify(orderRepository, times(1)).deleteById(any());
        verify(orderRepository, times(1)).existsById(any());
    }
    
    @Test
    void remove_notFound() {
        Long id = 1L;
        when(orderRepository.existsById(id)).thenReturn(false);
        
        assertThrows(OrderNotFoundException.class, () -> orderService.remove(id));
        
        verify(orderRepository, times(0)).deleteById(any());
        verify(orderRepository, times(1)).existsById(any());
    }
    
    @Test
    void getAllOrders() {
        Pageable pageable = mock(Pageable.class);
        Order order = OrderUtil.order();
        Page<Order> events = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(pageable)).thenReturn(events);
    
        Page<Order> allOrders = orderService.getAllOrders(pageable);
        
        assertThat(allOrders).contains(order);
        
    }
    
    private void setAuthenticationMocks(User user) {
        Authentication auth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    
        when(auth.getName()).thenReturn("user");
        when(userService.findByUsername(auth.getName())).thenReturn(user);
    }
}
