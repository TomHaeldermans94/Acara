package be.acara.events.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrder {
    Long eventId;
    int amountOfTickets;
}
