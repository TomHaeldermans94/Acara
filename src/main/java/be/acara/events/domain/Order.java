package be.acara.events.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Domain and entity class for an order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    /**
     * The id of the order
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The event from this order
     */
    @OneToOne
    private Event event;
    /**
     * The user that placed this order
     */
    @ManyToOne
    private User user;
    /**
     * The total price of this order
     */
    private BigDecimal total;
    /**
     * The amount of of tickets bought
     */
    private int amountOfTickets;
}
