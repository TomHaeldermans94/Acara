package be.acara.events.service;

import be.acara.events.domain.*;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.OrderNotFoundException;
import be.acara.events.repository.OrderRepository;
import be.acara.events.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final EventService eventService;
    private final UserService userService;
    private final MailService mailService;
    
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, EventService eventService, UserService userService, MailService mailService) {
        this.orderRepository = orderRepository;
        this.eventService = eventService;
        this.userService = userService;
        this.mailService = mailService;
    }
    
    /**
     * Returns an order given an id
     *
     * @param id the id to find the order with
     * @return the matching order
     * @throws OrderNotFoundException when the given id doesn't yield any result
     */
    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with ID %d not found", id)));
    }
    
    /**
     * Persists an order in the repository
     *
     * @param createOrder a {@link CreateOrder} containing the order details
     * @return the created and processed order
     */
    @Override
    public Order create(CreateOrder createOrder) {
        return orderRepository.saveAndFlush(createOrderHelper(createOrder));
    }
    
    /**
     * Batch operation of {@link #create(CreateOrder)}
     *
     * @param createOrderList a CreateOrderList containing a list of CreateOrder, each containing order details
     */
    @Override
    public void createAll(CreateOrderList createOrderList) {
        mailService.sendMessageWithAttachment(createOrderList, userService.getCurrentUser());
        orderRepository.saveAll(
                createOrderList.getOrders().stream()
                        .map(this::createOrderHelper)
                        .collect(Collectors.toList())
        );
    }
    
    /**
     * Helper method to map a {@link CreateOrder} to {@link Order}
     *
     * @param createOrder the createOrder to map and calculate
     * @return the calculated order
     */
    private Order createOrderHelper(CreateOrder createOrder) {
        Event event = eventService.findById(createOrder.getEventId());
        User user = userService.getCurrentUser();
        event.addAttendee(user);
        return Order.builder()
                .event(event)
                .user(user)
                .amountOfTickets(createOrder.getAmountOfTickets())
                .total(event.getPrice().multiply(new BigDecimal(createOrder.getAmountOfTickets())))
                .build();
    }
    
    /**
     * Edit orders
     *
     * @param id    the id of the order to edit
     * @param order the new body of the order
     * @return the edited order
     * @throws IdNotFoundException when the passed id and the id of the order doesn't match
     */
    @Override
    public Order edit(Long id, Order order) {
        if (!order.getId().equals(id)) {
            throw new IdNotFoundException(String.format("Id of order to edit does not match given id. Event id = %d, and given id = %d", order.getId(), id));
        }
        return orderRepository.saveAndFlush(order);
    }
    
    /**
     * Deletes the order with given id
     *
     * @param id the id of the order to delete
     * @throws OrderNotFoundException when the passed id doesn't exist
     */
    @Override
    public void remove(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(String.format("Order with ID %d not found", id));
        }
        orderRepository.deleteById(id);
    }
    
    
    /**
     * Returns all orders
     *
     * @param pageable a pageable to sort and filter with
     * @return a page of order matching the pageable results
     */
    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
