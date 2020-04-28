package be.acara.events.service;

import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order findById(Long id);

    Order create(CreateOrder createOrder);

    Order edit(Long id, Order order);

    void remove(Long id);

    Page<Order> getAllOrders(Pageable pageable);
}
