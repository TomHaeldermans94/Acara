package be.acara.events.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class CreateOrderList {
    private Set<CreateOrder> orders;
}
