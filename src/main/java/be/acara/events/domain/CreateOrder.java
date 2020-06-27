package be.acara.events.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * A domain class to create orders with.
 */
@Getter
@AllArgsConstructor
@Builder
public class CreateOrder {
    /**
     * The event id to create an order for
     */
    private Long eventId;
    /**
     * The amount of tickets that are being ordered
     */
    private int amountOfTickets;
}
