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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Order order = (Order) o;
        
        if (amountOfTickets != order.amountOfTickets) return false;
        if (!id.equals(order.id)) return false;
        if (!event.equals(order.event)) return false;
        if (!user.equals(order.user)) return false;
        return total.equals(order.total);
    }
    
    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + event.getName().hashCode();
        result = 31 * result + user.getUsername().hashCode();
        result = 31 * result + amountOfTickets;
        return result;
    }
}
