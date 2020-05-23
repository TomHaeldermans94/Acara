package be.acara.events.domain;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderList {
    private Set<CreateOrder> orders = new HashSet<>();
}
