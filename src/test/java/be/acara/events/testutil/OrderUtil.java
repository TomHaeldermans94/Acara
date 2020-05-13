package be.acara.events.testutil;

import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.Order;

public class OrderUtil {
    
    public static CreateOrder createOrder() {
        return CreateOrder.builder()
                .eventId(1L)
                .amountOfTickets(1)
                .build();
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
}
