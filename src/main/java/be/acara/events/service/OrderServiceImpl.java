package be.acara.events.service;

import be.acara.events.controller.dto.TicketDto;
import be.acara.events.domain.*;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.OrderNotFoundException;
import be.acara.events.repository.OrderRepository;
import be.acara.events.service.mail.MailService;
import be.acara.events.service.pdf.PdfService;
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
    private final PdfService pdfService;
    
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, EventService eventService, UserService userService, MailService mailService, PdfService pdfService) {
        this.orderRepository = orderRepository;
        this.eventService = eventService;
        this.userService = userService;
        this.mailService = mailService;
        this.pdfService = pdfService;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with ID %d not found", id)));
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Order create(CreateOrder createOrder) {
        return orderRepository.saveAndFlush(createOrderHelper(createOrder));
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createAll(CreateOrderList createOrderList) {
        List<Order> createdOrders = orderRepository.saveAll(createOrderList.getOrders().stream()
                .map(this::createOrderHelper)
                .collect(Collectors.toList()));
    
    
        mailService.sendMessageWithAttachment(createdOrders, userService.getCurrentUser());
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
     * {@inheritDoc}
     */
    @Override
    public Order edit(Long id, Order order) {
        if (!order.getId().equals(id)) {
            throw new IdNotFoundException(String.format("Id of order to edit does not match given id. Event id = %d, and given id = %d", order.getId(), id));
        }
        return orderRepository.saveAndFlush(order);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException(String.format("Order with ID %d not found", id));
        }
        orderRepository.deleteById(id);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TicketDto getTicketFromEvent(Long eventId) {
        Event event = eventService.findById(eventId);
        User user = userService.getCurrentUser();
        Order order = orderRepository.findByEventAndUser(event,user);
        return new TicketDto(pdfService.createTicketPdf(order));
    }
}
