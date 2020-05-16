package be.acara.events.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    Long id;
    EventDto event;
    UserDto user;
    BigDecimal total;
    int amountOfTickets;
}
