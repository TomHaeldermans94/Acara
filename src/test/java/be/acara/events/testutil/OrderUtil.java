package be.acara.events.testutil;

import be.acara.events.controller.dto.OrderDto;
import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.CreateOrderList;
import be.acara.events.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OrderUtil {
    
    public static CreateOrder createOrder() {
        return CreateOrder.builder()
                .eventId(1L)
                .amountOfTickets(1)
                .build();
    }

    public static CreateOrderList createOrderList() {
        return new CreateOrderList(Set.of(createOrder()));
    }
    
    public static Order order() {
        return Order.builder()
                .amountOfTickets(1)
                .event(EventUtil.firstEvent())
                .total(EventUtil.firstEvent().getPrice())
                .user(UserUtil.firstUser())
                .id(1L)
                .build();
    }
    
    public static OrderDto orderDto() {
        return OrderDto.builder()
                .amountOfTickets(1)
                .event(EventUtil.map(EventUtil.firstEvent()))
                .total(EventUtil.firstEvent().getPrice())
                .user(UserUtil.map(UserUtil.firstUser()))
                .id(1L)
                .build();
    }
    
    public static Page<Order> orderPage() {
        Order secondOrder = Order.builder()
                .id(2L)
                .user(UserUtil.secondUser())
                .total(BigDecimal.TEN)
                .event(EventUtil.secondEvent())
                .amountOfTickets(2)
                .build();
        
        return new PageImpl<>(
                new ArrayList<>(
                        List.of(order(), secondOrder)));
    }
}
