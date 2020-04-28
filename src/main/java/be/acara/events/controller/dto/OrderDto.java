package be.acara.events.controller.dto;

import be.acara.events.domain.Event;
import be.acara.events.domain.User;
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
    Event event;
    User user;
    BigDecimal total;
    int amountOfTickets;
}
