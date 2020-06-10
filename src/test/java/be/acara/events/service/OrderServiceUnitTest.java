package be.acara.events.service;

import be.acara.events.domain.*;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.OrderNotFoundException;
import be.acara.events.repository.OrderRepository;
import be.acara.events.service.mail.MailService;
import be.acara.events.service.pdf.PdfService;
import be.acara.events.testutil.OrderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static be.acara.events.testutil.EventUtil.firstEvent;
import static be.acara.events.testutil.UserUtil.firstUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Unit Test")
class OrderServiceUnitTest {
    /*******************************
     *         dependencies        *
     *******************************/
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;
    @Mock
    private MailService mailService;
    @Mock
    private PdfService pdfService;
    
    /*******************************
     *         Class to test       *
     *******************************/
    private OrderService orderService;
    
    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, eventService, userService, mailService, pdfService);
    }
    
    @Nested
    @DisplayName("Find orders")
    class Find {
        @Test
        @DisplayName("Find an order by a provided id")
        void findById() {
            Long id = 1L;
            Order order = mock(Order.class);
            when(orderRepository.findById(id)).thenReturn(Optional.of(order));
            
            Order answer = orderService.findById(id);
            
            assertThat(answer).isEqualTo(order);
            verify(orderRepository, times(1)).findById(id);
        }
        
        @Test
        @DisplayName("Find an order by a provided id - not found")
        void findById_notFound() {
            when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
            
            assertThrows(OrderNotFoundException.class, () -> orderService.findById(Long.MAX_VALUE));
            
            verify(orderRepository, times(1)).findById(Long.MAX_VALUE);
        }
        
        @Test
        @DisplayName("Get all orders")
        void getAllOrders() {
            Pageable pageable = mock(Pageable.class);
            Order order = OrderUtil.order();
            Page<Order> events = new PageImpl<>(List.of(order));
            when(orderRepository.findAll(pageable)).thenReturn(events);
            
            Page<Order> allOrders = orderService.getAllOrders(pageable);
            
            assertThat(allOrders).contains(order);
            
        }
    }
    
    @Nested
    @DisplayName("Create orders")
    class Create {
        @Test
        @DisplayName("Create an order")
        void create() {
            User user = firstUser();
            Event event = firstEvent();
            CreateOrder createOrder = OrderUtil.createOrder();
            Order order = OrderUtil.order();
            
            when(eventService.findById(anyLong())).thenReturn(event);
            when(userService.getCurrentUser()).thenReturn(user);
            when(orderRepository.saveAndFlush(any())).thenReturn(order);
            
            Order answer = orderService.create(createOrder);
            
            assertThat(answer.getEvent().getId()).isEqualTo(createOrder.getEventId());
            assertThat(answer.getAmountOfTickets()).isEqualTo(createOrder.getAmountOfTickets());
            
            verify(orderRepository, times(1)).saveAndFlush(any());
            verify(eventService, times(1)).findById(anyLong());
        }
        
        @Test
        @DisplayName("Create multiple orders through a list of multiple singular orders")
        void create_batch() {
            User user = firstUser();
            Event event = firstEvent();
            CreateOrder createOrder = OrderUtil.createOrder();
            CreateOrderList createOrderList = new CreateOrderList(Set.of(createOrder));
            
            when(eventService.findById(anyLong())).thenReturn(event);
            when(userService.getCurrentUser()).thenReturn(user);
            when(orderRepository.saveAll(any())).thenReturn(null);
            
            orderService.createAll(createOrderList);
            
            verify(orderRepository, times(1)).saveAll(any());
            verify(eventService, times(1)).findById(anyLong());
        }
    }
    
    @Nested
    @DisplayName("Edit orders")
    class Edit {
        @Test
        @DisplayName("Edits an order")
        void edit() {
            Order order = OrderUtil.order();
            when(orderRepository.saveAndFlush(any())).thenReturn(order);
            
            Order answer = orderService.edit(order.getId(), order);
            
            assertThat(answer).isEqualTo(order);
            
            verify(orderRepository, times(1)).saveAndFlush(any());
        }
        
        @Test
        @DisplayName("Edits an order - mismatching id")
        void edit_wrongId() {
            Order order = OrderUtil.order();
            
            assertThrows(IdNotFoundException.class, () -> orderService.edit(2L, order));
            
            verify(orderRepository, times(0)).saveAndFlush(any());
        }
    }
    
    @Nested
    @DisplayName("Remove orders")
    class Remove {
        @Test
        @DisplayName("Removes an order")
        void remove() {
            Long id = 1L;
            when(orderRepository.existsById(id)).thenReturn(true);
            doNothing().when(orderRepository).deleteById(id);
            
            orderService.remove(id);
            
            verify(orderRepository, times(1)).deleteById(any());
            verify(orderRepository, times(1)).existsById(any());
        }
        
        @Test
        @DisplayName("Removes an order - id not found")
        void remove_notFound() {
            Long id = 1L;
            when(orderRepository.existsById(id)).thenReturn(false);
            
            assertThrows(OrderNotFoundException.class, () -> orderService.remove(id));
            
            verify(orderRepository, times(0)).deleteById(any());
            verify(orderRepository, times(1)).existsById(any());
        }
    }
}
