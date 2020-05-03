package be.acara.events.service;

import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.Event;
import be.acara.events.domain.Order;
import be.acara.events.domain.User;
import be.acara.events.exceptions.EventNotFoundException;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.OrderNotFoundException;
import be.acara.events.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, EventService eventService, UserService userService) {
        this.orderRepository = orderRepository;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with ID %d not found", id)));
    }


    @Override
    public Order create(CreateOrder createOrder) {
        Event event = eventService.findById(createOrder.getEventId());
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(userName);
        eventService.addAttendee(event, user);
        orderRepository.saveAndFlush(
                Order.builder()
                        .event(event)
                        .user(user)
                        .amountOfTickets(createOrder.getAmountOfTickets())
                        .total(event.getPrice().multiply(new BigDecimal(createOrder.getAmountOfTickets())))
                        .build());
        Order order = orderRepository.saveAndFlush(
                Order.builder()
                        .event(event)
                        .user(user)
                        .amountOfTickets(createOrder.getAmountOfTickets())
                        .total(event.getPrice().multiply(new BigDecimal(createOrder.getAmountOfTickets())))
                        .build());
        eventService.addAttendee(event, user);
        return order;
    }


    @Override
    public Order edit(Long id, Order order) {
        if (!order.getId().equals(id)) {
            throw new IdNotFoundException(String.format("Id of order to edit does not match given id. Event id = %d, and given id = %d", order.getId(), id));
        }
        return orderRepository.saveAndFlush(order);
    }

    @Override
    public void remove(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EventNotFoundException(String.format("Order with ID %d not found", id));
        }
        orderRepository.deleteById(id);
    }


    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
