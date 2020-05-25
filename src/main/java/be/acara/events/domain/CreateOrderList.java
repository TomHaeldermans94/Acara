package be.acara.events.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * A wrapper class for a set of {@link CreateOrder}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderList {
    /**
     * The set of {@link CreateOrder}
     */
    private Set<CreateOrder> orders = new HashSet<>();
}
