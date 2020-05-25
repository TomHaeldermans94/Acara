package be.acara.events.service;

import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.Order;
import be.acara.events.exceptions.IdNotFoundException;
import be.acara.events.exceptions.OrderNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    
    /**
     * Returns an order given an id
     *
     * @param id the id to find the order with
     * @return the matching order
     * @throws OrderNotFoundException when the given id doesn't yield any result
     */
    Order findById(Long id);
    
    /**
     * Persists an order in the repository
     *
     * @param createOrder a {@link CreateOrder} containing the order details
     * @return the created and processed order
     */
    Order create(CreateOrder createOrder);
    
    /**
     * Batch operation of {@link #create(CreateOrder)}
     *
     * @param createOrderList a CreateOrderList containing a list of CreateOrder, each containing order details
     */
    void createAll(CreateOrderList createOrderList);
    
    /**
     * Edit orders
     *
     * @param id    the id of the order to edit
     * @param order the new body of the order
     * @return the edited order
     * @throws IdNotFoundException when the passed id and the id of the order doesn't match
     */
    Order edit(Long id, Order order);
    
    /**
     * Deletes the order with given id
     *
     * @param id the id of the order to delete
     * @throws OrderNotFoundException when the passed id doesn't exist
     */
    void remove(Long id);
    
    /**
     * Returns all orders
     *
     * @param pageable a pageable to sort and filter with
     * @return a page of order matching the pageable results
     */
    Page<Order> getAllOrders(Pageable pageable);
}
