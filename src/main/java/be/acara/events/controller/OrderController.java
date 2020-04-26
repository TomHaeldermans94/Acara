package be.acara.events.controller;

import be.acara.events.controller.dto.CreateOrderDto;
import be.acara.events.controller.dto.OrderList;
import be.acara.events.domain.Order;
import be.acara.events.service.OrderService;
import be.acara.events.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<CreateOrderDto> findById(@PathVariable("id") Long id) {
//        return ResponseEntity.ok(
//                orderMapper.orderToOrderDto(orderService.findById(id)));
//    }

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderDto createOrderDto) {
        Order order = orderService.create(orderMapper.orderDtoToOrder(createOrderDto));
        URI uri = URI.create(String.format("/api/orders/%d", order.getId()));
        return ResponseEntity.created(uri).build();
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<CreateOrderDto> editOrder(@PathVariable("id") Long id, @RequestBody @Valid CreateOrderDto createOrderDto) {
//        Order order = orderService.edit(id, orderMapper.orderDtoToOrder(createOrderDto));
//        return ResponseEntity.ok(orderMapper.orderToOrderDto(order));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeOrder(@PathVariable("id") Long id) {
        orderService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<OrderList> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orderMapper.pageToOrderList(orderPage));
    }
}
