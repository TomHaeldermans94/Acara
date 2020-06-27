package be.acara.events.service.mapper;

import be.acara.events.controller.dto.EventDto;
import be.acara.events.controller.dto.OrderDto;
import be.acara.events.controller.dto.OrderDtoList;
import be.acara.events.controller.dto.UserDto;
import be.acara.events.domain.Order;
import be.acara.events.testutil.OrderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {
    
    private OrderMapper orderMapper;
    
    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapperImpl();
    }
    
    @Test
    void pageToOrderList() {
        Page<Order> orders = OrderUtil.orderPage();
    
        OrderDtoList answer = orderMapper.pageToOrderList(orders);
        
        assertThat(answer.getSize()).isEqualTo(2);
        assertThat(answer.getContent()).extracting(OrderDto::getAmountOfTickets).containsExactly(1,2);
        assertThat(answer.getContent()).extracting(OrderDto::getId).containsExactly(1L,2L);
        assertThat(answer.getContent()).extracting(OrderDto::getTotal).containsExactly(BigDecimal.TEN, BigDecimal.TEN);
        assertThat(answer.getContent()).extracting(OrderDto::getEvent).extracting(EventDto::getId).containsExactly(1L,2L);
        assertThat(answer.getContent()).extracting(OrderDto::getUser).extracting(UserDto::getId).containsExactly(1L,2L);
        
    }
}
