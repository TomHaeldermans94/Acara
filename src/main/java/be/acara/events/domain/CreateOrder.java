package be.acara.events.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateOrder {
    Long eventId;
    int amountOfTickets;
}
