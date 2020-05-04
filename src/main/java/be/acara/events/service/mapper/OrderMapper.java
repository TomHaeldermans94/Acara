package be.acara.events.service.mapper;

import be.acara.events.controller.dto.CreateOrderDto;
import be.acara.events.controller.dto.OrderDto;
import be.acara.events.controller.dto.OrderList;
import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    CreateOrderDto orderToOrderDto(CreateOrder order);

    CreateOrder orderDtoToOrder(CreateOrderDto order);
    
    OrderDto orderToOrderDto(Order order);

    default OrderList pageToOrderList(Page<Order> page) {
        return new OrderList(
                page.getContent().stream()
                        .map(this::orderToOrderDto)
                        .collect(Collectors.toList())
                , page.getPageable(), page.getTotalElements());
    }
}
