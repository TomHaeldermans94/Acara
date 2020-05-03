package be.acara.events.service.mapper;

import be.acara.events.controller.dto.CreateOrderDto;
import be.acara.events.controller.dto.OrderList;
import be.acara.events.domain.CreateOrder;
import be.acara.events.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.Collections;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    CreateOrderDto orderToOrderDto(CreateOrder order);

    CreateOrder orderDtoToOrder(CreateOrderDto order);

    default OrderList pageToOrderList(Page<Order> page) {
//        List<CreateOrderDto> collect = page.getContent().stream().collect(Collectors.toList());
        return new OrderList(Collections.EMPTY_LIST , page.getPageable(), page.getTotalElements());
    }
}
